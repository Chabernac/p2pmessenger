/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.cam;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import chabernac.protocol.Protocol;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.message.Message;
import chabernac.protocol.message.MessageProtocol;
import chabernac.protocol.packet.Packet;
import chabernac.protocol.packet.PacketProtocolException;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;
import chabernac.web.JPGWebCapture;

public class CamProtocol extends Protocol {
  private static Logger LOGGER = Logger.getLogger( CamProtocol.class );

  public static String ID = "CAM";
  private JPGWebCapture myCapture = new JPGWebCapture();
  
  private List<iCamListener> myCamListeners = new ArrayList<iCamListener>();
  
  private static enum Command {CAPTURE};
  
  public CamProtocol(){
    super(ID);
  }
  
  private MessageProtocol getMessageProtocol() throws ProtocolException{
    return (MessageProtocol)findProtocolContainer().getProtocol(MessageProtocol.ID);
  }
  
  private RoutingTable getRoutingTable() throws ProtocolException{
    return ((RoutingProtocol)findProtocolContainer().getProtocol( RoutingProtocol.ID )).getRoutingTable();
  }

  
  public void requestCapture(String aPeerId, int aWidth, int aHeight, float aQuality) throws PacketProtocolException{
    Message theMessage = new Message();
    theMessage.setDestination(getRoutingTable().getEntryForPeer(aPeerId).getPeer());
    theMessage.setMessage(createMessage(Command.CAPTURE.name() + ";" + aWidth + ";" + aHeight + ";" + aQuality ));
    getMessageProtocol().sendMessage(theMessage);
  }
  

  @Override
  public void handlePacket( Packet aPacket ) {
    try{
    if(aPacket.getBytesAsString().startsWith( Command.CAPTURE.name() )){
      String[] theParams = aPacket.getBytesAsString().split( ";" );
      int theWidth = Integer.parseInt(theParams[1]);
      int theHeight= Integer.parseInt(theParams[2]);
      float theQuality = Float.parseFloat(theParams[3]);
      byte[] theBytes = myCapture.capture(theWidth, theHeight, theQuality);
      Packet theCapturePacket = new Packet( aPacket.getFrom(), ID, theBytes, 5, false );
      myPacketProtocol.sendPacket( theCapturePacket );
    } else {
      //the packet is a snapshot
      BufferedImage theImage = ImageIO.read(new ByteArrayInputStream(aPacket.getBytes()));
      for(iCamListener theListener : myCamListeners){
        theListener.imageReceived( theImage );
      }
    }
    }catch(Exception e){
      LOGGER.error("Error occured while handling packet", e);
    }
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

  @Override
  public String handleCommand(String aSessionId, String anInput) {
    // TODO Auto-generated method stub
    return null;
  }
}
