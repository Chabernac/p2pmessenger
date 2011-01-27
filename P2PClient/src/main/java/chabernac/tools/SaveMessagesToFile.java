/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import chabernac.protocol.facade.P2PFacade;
import chabernac.protocol.facade.P2PFacadeException;
import chabernac.protocol.message.DeliveryReport;
import chabernac.protocol.message.MultiPeerMessage;
import chabernac.protocol.message.iDeliverReportListener;
import chabernac.protocol.message.iMultiPeerMessageListener;
import chabernac.protocol.message.DeliveryReport.Status;

public class SaveMessagesToFile implements iMultiPeerMessageListener, iDeliverReportListener {
  private static final Logger LOGGER = Logger.getLogger(SaveMessagesToFile.class);
  private static final SimpleDateFormat FORMAT = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
  private final File myFile;
  private final P2PFacade myFacade;

  public SaveMessagesToFile( File aFile, P2PFacade aFacade ) throws P2PFacadeException {
    myFile = aFile;
    myFacade = aFacade;
    aFacade.addMessageListener( this );
    aFacade.addDeliveryReportListener( this );
  }

  @Override
  public synchronized void messageReceived( MultiPeerMessage aMessage ) {
    try {
      writeLine( formatMessage( aMessage ) );
    } catch ( Exception e ) {
      LOGGER.error("Unable to save received message", e);
    }
  }
  
  private synchronized void writeLine(String aLine) throws FileNotFoundException{
    PrintWriter theWriter = null;
    try{
      theWriter = new PrintWriter( new OutputStreamWriter( new FileOutputStream( myFile, true ) ) );
      theWriter.println(aLine);
      theWriter.flush();
    } finally  {
      theWriter.close();
    }    
  }

  @Override
  public void acceptDeliveryReport( DeliveryReport aDeliverReport ) {
    if(aDeliverReport.getDeliveryStatus() == Status.IN_PROGRESS) return;
    try {
      writeLine( formatMessage( aDeliverReport ) );
    } catch ( Exception e ) {
      LOGGER.error("Unable to save received message", e);
    }
  }
  
  private String formatTime(MultiPeerMessage aMessage){
    return FORMAT.format( aMessage.getCreationTime() );
  }
  
  private String formatMessage( MultiPeerMessage aMessage ) throws P2PFacadeException {
    return formatTime( aMessage ) + ": " + Tools.getEnvelop(myFacade, aMessage) + ": " + aMessage.getMessage();
  }

  private String formatMessage( DeliveryReport aDeliverReport ) throws P2PFacadeException {
    return formatTime( aDeliverReport.getMultiPeerMessage() ) + ": " + Tools.getEnvelop( myFacade, aDeliverReport.getMessage()) + ": " + aDeliverReport.getMultiPeerMessage().getMessage() + " [" + aDeliverReport.getDeliveryStatus() + "]";
  }

}
