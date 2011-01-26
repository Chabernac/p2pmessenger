/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

import chabernac.protocol.facade.P2PFacade;
import chabernac.protocol.facade.P2PFacadeException;
import chabernac.protocol.message.MultiPeerMessage;
import chabernac.protocol.message.iMultiPeerMessageListener;

public class SaveMessagesToFile implements iMultiPeerMessageListener {
  private final static Logger LOGGER = Logger.getLogger(SaveMessagesToFile.class);
  
  private final File myFile;
  private final P2PFacade myFacade;

  public SaveMessagesToFile( File aFile, P2PFacade aFacade ) throws P2PFacadeException {
    myFile = aFile;
    myFacade = aFacade;
    aFacade.addMessageListener( this );
  }

  @Override
  public synchronized void messageReceived( MultiPeerMessage aMessage ) {
    PrintWriter theWriter = null;
    try{
      theWriter = new PrintWriter( new OutputStreamWriter( new FileOutputStream( myFile, true ) ) );
      theWriter.println(formatMessage(aMessage));
      theWriter.flush();
    }catch(Exception e){
      LOGGER.error( "Could not save message to file", e );
    } finally  {
      theWriter.close();
    }
  }

  private String formatMessage( MultiPeerMessage aMessage ) throws P2PFacadeException {
    return Tools.getEnvelop(myFacade, aMessage) + ": " + aMessage.getMessage();
  }

}
