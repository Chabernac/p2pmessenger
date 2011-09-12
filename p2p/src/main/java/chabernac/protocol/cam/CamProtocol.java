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

import chabernac.protocol.packet.AbstractPacketProtocol;
import chabernac.protocol.packet.Packet;
import chabernac.protocol.packet.PacketProtocolException;
import chabernac.webcam.JPGWebCamCapture;

public class CamProtocol extends AbstractPacketProtocol {
  private static Logger LOGGER = Logger.getLogger( CamProtocol.class );

  public static String ID = "CAM";
  private JPGWebCamCapture myCapture = new JPGWebCamCapture();
  
  private List<iCamListener> myCamListeners = new ArrayList<iCamListener>();
  
  private static enum Command {CAPTURE};
  
  @Override
  public String getId() {
    return ID;
  }
  
  public void requestCapture(String aPeerId, int aWidth, int aHeight, float aQuality) throws PacketProtocolException{
    Packet theCapturePacket = new Packet( aPeerId, ID, Command.CAPTURE.name() + ";" + aWidth + ";" + aHeight + ";" + aQuality, 5, false );
    myPacketProtocol.sendPacket( theCapturePacket );
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
}
