/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.web;

import java.awt.Frame;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.smaxe.os.jna.win32.support.IVideoFrameProcessor;
import com.smaxe.os.jna.win32.support.VideoCaptureDevice;
import com.smaxe.os.jna.win32.support.VideoCaptureLibrary;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.W32API.HWND;

public class WebCamDecorator {
  private final JVideoScreen myVideoScreen;
  private VideoCaptureDevice myCaptureDevice;
  private int myCounter = 0;
  private Pointer myFramePointer = null;

  public WebCamDecorator( JVideoScreen aVideoScreen ) {
    super();
    myVideoScreen = aVideoScreen;
    init();
  }

  private void init(){
    List<VideoCaptureDevice> theDevices = VideoCaptureLibrary.findAllVideoCaptureDevices();
    if(theDevices.size() > 0){
      myCaptureDevice = theDevices.get(0);
      myCaptureDevice.setFrameFlip(false);
    }
    
    JFrame theTestFrame = new JFrame();
    theTestFrame.setUndecorated( true );
    theTestFrame.setSize( 0, 0 );
    theTestFrame.setVisible( true );
    
    myFramePointer = Native.getComponentPointer(theTestFrame);
    
    theTestFrame.setVisible(false);
  }

  public void start(){
    if(myCaptureDevice == null) return;
    
    myCaptureDevice.startVideoCapture(
        new HWND(myFramePointer), 
        myVideoScreen.getWidth(), 
        myVideoScreen.getHeight(), 
        new IVideoFrameProcessor() {
          public void onFrame(final int width, final int height, final byte[] rgb, int components){
            SwingUtilities.invokeLater(new Runnable(){
              public void run(){
                myVideoScreen.setFrameSize(width, height);
                myVideoScreen.setFrame(rgb);
              }
            });
          }
        });
    
  }

  public void stop(){
    myCaptureDevice.stopVideoCapture();
  }

}
