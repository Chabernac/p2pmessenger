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
import chabernac.protocol.message.Message;
import chabernac.protocol.message.MessageException;
import chabernac.protocol.message.MessageProtocol;
import chabernac.protocol.pipe.IPipeListener;
import chabernac.protocol.pipe.Pipe;
import chabernac.protocol.pipe.PipeException;
import chabernac.protocol.pipe.PipeProtocol;
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
  
  public void sendMessage(Message aMessage) throws MessageException, ProtocolException{
    ((MessageProtocol)myContainer.getProtocol( MessageProtocol.ID )).sendMessage( aMessage );
  }
  
  public void sendEnctryptedMessage(Message aMessage) throws MessageException, ProtocolException{
    ((MessageProtocol)myContainer.getProtocol( MessageProtocol.ID )).sendEncryptedMessage( aMessage );
  }
  
  public void openPipe(Pipe aPipe) throws PipeException, ProtocolException{
    ((PipeProtocol)myContainer.getProtocol( MessageProtocol.ID )).openPipe( aPipe );
  }
  
  public void closePipe(Pipe aPipe) throws PipeException, ProtocolException{
    ((PipeProtocol)myContainer.getProtocol( MessageProtocol.ID )).closePipe( aPipe );
  }
  
  public void addPipeListener(IPipeListener aPipeListener) throws ProtocolException{
    ((PipeProtocol)myContainer.getProtocol( MessageProtocol.ID )).addPipeListener( aPipeListener );
  }
  
  
}
