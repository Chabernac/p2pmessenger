/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.cam;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import chabernac.protocol.IProtocol;
import chabernac.protocol.Protocol;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.message.Message;
import chabernac.protocol.message.MessageProtocol;
import chabernac.protocol.packet.Packet;
import chabernac.protocol.packet.PacketProtocol;
import chabernac.protocol.packet.iPacketListener;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;
import chabernac.web.JPGWebCapture;

public class CamProtocol extends Protocol {
  private static Logger LOGGER = Logger.getLogger( CamProtocol.class );

  public static String ID = "CAM";
  private JPGWebCapture myCapture = new JPGWebCapture();

  private List<iCamListener> myCamListeners = new ArrayList<iCamListener>();

  private static enum Command {CAPTURE};
  private static enum Response{SNAPSHOT_SEND, SNAPSHOT_FAILED, UNKNOWN_COMMAND};

  private ExecutorService myExecutorService = Executors.newSingleThreadExecutor();

  public CamProtocol() throws ProtocolException{
    super(ID);
  }
  
  public void setMasterProtocol(IProtocol aProtocol){
    super.setMasterProtocol( aProtocol );
    try {
      addPacketListener();
    } catch ( ProtocolException e ) {
      LOGGER.error("Could not add packet listener", e);
    }
  }

  private void addPacketListener() throws ProtocolException{
    getPacketProtocol().addPacketListenr( ID, new PacketListener() );
  }

  private MessageProtocol getMessageProtocol() throws ProtocolException{
    return (MessageProtocol)findProtocolContainer().getProtocol(MessageProtocol.ID);
  }
  
  private PacketProtocol getPacketProtocol() throws ProtocolException{
    return (PacketProtocol)findProtocolContainer().getProtocol(PacketProtocol.ID);
  }

  private RoutingTable getRoutingTable() throws ProtocolException{
    return ((RoutingProtocol)findProtocolContainer().getProtocol( RoutingProtocol.ID )).getRoutingTable();
  }


  public void requestCapture(String aPeerId, int aWidth, int aHeight, float aQuality) throws CamProtocolException{
    try{
      Message theMessage = new Message();
      theMessage.setDestination(getRoutingTable().getEntryForPeer(aPeerId).getPeer());
      theMessage.setProtocolMessage( true );
      theMessage.setMessage(createMessage(Command.CAPTURE.name() + ";"  + getRoutingTable().getLocalPeerId() + ";" + aWidth + ";" + aHeight + ";" + aQuality ));
      String theResponse = getMessageProtocol().sendMessage(theMessage);
      if(!theResponse.equalsIgnoreCase( Response.SNAPSHOT_SEND.name() )){
        LOGGER.error( "Snapshot failed '" + theResponse + "'" );
        throw new CamProtocolException("Taking snaphsot failed '" + theResponse + "'");
      }
    }catch(Exception e){
      throw new CamProtocolException( "Could not send message", e );
    }
  }

  @Override
  public String handleCommand(String aSessionId, String anInput) {
    if(anInput.startsWith( Command.CAPTURE.name() )){
      String[] theParams = anInput.split( ";" );
      final String theFrom = theParams[1];
      final int theWidth = Integer.parseInt(theParams[2]);
      final int theHeight= Integer.parseInt(theParams[3]);
      final float theQuality = Float.parseFloat(theParams[4]);

      if(!myCapture.isReady()){
        return Response.SNAPSHOT_FAILED.name() + " device not ready or unavailable";
      }
      
      myExecutorService.execute( new Runnable(){
        public void run(){
          try{
            byte[] theBytes = myCapture.capture(theWidth, theHeight, theQuality);

            Packet theCapturePacket = new Packet( theFrom, Long.toString(System.currentTimeMillis()), ID, theBytes, 5, false );
            getPacketProtocol().sendPacket( theCapturePacket );
          }catch(Exception e){
            LOGGER.error("Snapshot failed ", e);
          }    
        }
      });

      return Response.SNAPSHOT_SEND.name();
    }
    
    return Response.UNKNOWN_COMMAND.name();
  }

  public void addCamListener(iCamListener aListener){
    myCamListeners.add(aListener);
  }

  @Override
  public void stop() {
    myCapture.stop();
  }

  @Override
  public String getDescription() {
    return "cam protocol";
  }

  private class PacketListener implements iPacketListener {
    @Override
    public void packetDelivered( String aPacketId ) {
      LOGGER.debug("Packet with id '" + aPacketId + "' was delivered");

    }

    @Override
    public void packetDeliveryFailed( String aPacketId ) {
      LOGGER.error("Packet with id '" + aPacketId + "' could not be delivered");
    }

    @Override
    public void packetReceived( Packet aPacket ) {
      //the packet is a snapshot
      try{
        BufferedImage theImage = ImageIO.read(new ByteArrayInputStream(aPacket.getBytes()));
        for(iCamListener theListener : myCamListeners){
          theListener.imageReceived( theImage );
        }
      }catch(Exception e){
        LOGGER.error("Arror occured while reading received snapshot", e);
      }

    }
  }
}
