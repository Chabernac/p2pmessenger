/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.testingutils;

import java.io.IOException;

import chabernac.protocol.pipe.IPipeListener;
import chabernac.protocol.pipe.Pipe;
import chabernac.tools.IOTools;

public class EchoPipeListener implements IPipeListener{
  private Pipe myPipe = null;
  
  
  @Override
  public void incomingPipe(final Pipe aPipe) {
    myPipe = aPipe;
    new Thread(new Runnable(){
      public void run(){
        try {
          IOTools.copyStream(aPipe.getSocket().getInputStream(), aPipe.getSocket().getOutputStream());
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }
  
  public Pipe getPipe(){
    return myPipe;
  }
}