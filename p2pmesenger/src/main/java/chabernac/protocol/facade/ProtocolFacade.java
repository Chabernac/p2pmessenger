/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.facade;

import java.io.File;

import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.filetransfer.FileTransferException;
import chabernac.protocol.filetransfer.FileTransferProtocol;
import chabernac.protocol.filetransfer.iFileHandler;
import chabernac.protocol.routing.Peer;

public class ProtocolFacade {
  private ProtocolContainer myContainer = null;
  
  public ProtocolFacade(ProtocolContainer aContainer){
    myContainer = aContainer;
  }
  
  public void sendFile(File aFile, Peer aPeer) throws FileTransferException, ProtocolException{
    ((FileTransferProtocol)myContainer.getProtocol( FileTransferProtocol.ID )).sendFile( aFile, aPeer );
  }
  
  public void setFileHandler(iFileHandler aFileHandler) throws ProtocolException{
    ((FileTransferProtocol)myContainer.getProtocol( FileTransferProtocol.ID )).setFileHandler( aFileHandler );
  }
}
