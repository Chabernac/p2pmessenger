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

import chabernac.protocol.packet.Packet;
import chabernac.protocol.packet.PacketProtocol;
import chabernac.protocol.packet.iPacketListener;
import chabernac.protocol.packet.iPacketProtocol;
import chabernac.webcam.JPGWebCamCapture;

public class CamProtocol implements iPacketProtocol {
  private static Logger LOGGER = Logger.getLogger( CamProtocol.class );

  public static String ID = "CAM";
  private final PacketProtocol myPacketProtocol;
  private JPGWebCamCapture myCapture = new JPGWebCamCapture( 320, 240, 0.5f);
  
  private List<iCamListener> myCamListeners = new ArrayList<iCamListener>();
  
  private static enum Command {CAPTURE};
  
  public CamProtocol( PacketProtocol aPacketProtocol ) {
    myPacketProtocol = aPacketProtocol;
    init();
  }
  
  private void init(){
    myPacketProtocol.addPacketProtocol( this );
  }

  @Override
  public String getId() {
    return ID;
  }
  

  @Override
  public void handlePacket( Packet aPacket ) {
    try{
    if(aPacket.getBytesAsString().equalsIgnoreCase( Command.CAPTURE.name() )){
      byte[] theBytes = myCapture.capture();
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
  
  
  private class PacketListener implements iPacketListener {

    @Override
    public void packetDelivered( String aPacketId ) {

    }

    @Override
    public void packetDeliveryFailed( String aPacketId ) {
      // TODO Auto-generated method stub

    }

    @Override
    public void packetReceived( Packet aPacket ) {
      // TODO Auto-generated method stub

    }
  }




}
