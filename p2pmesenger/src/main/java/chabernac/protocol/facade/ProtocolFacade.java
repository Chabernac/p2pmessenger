/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.facade;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.ProtocolFactory;
import chabernac.protocol.ProtocolServer;
import chabernac.protocol.filetransfer.FileTransferException;
import chabernac.protocol.filetransfer.FileTransferProtocol;
import chabernac.protocol.filetransfer.iFileHandler;
import chabernac.protocol.message.Message;
import chabernac.protocol.message.MessageException;
import chabernac.protocol.message.MessageIndicator;
import chabernac.protocol.message.MessageProtocol;
import chabernac.protocol.message.MultiPeerMessage;
import chabernac.protocol.message.MultiPeerMessageProtocol;
import chabernac.protocol.pipe.IPipeListener;
import chabernac.protocol.pipe.Pipe;
import chabernac.protocol.pipe.PipeException;
import chabernac.protocol.pipe.PipeProtocol;
import chabernac.protocol.routing.Peer;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.userinfo.UserInfo;
import chabernac.protocol.userinfo.UserInfoProtocol;
import chabernac.protocol.userinfo.iUserInfoListener;
import chabernac.protocol.userinfo.iUserInfoProvider;

public class ProtocolFacade {
  private ProtocolContainer myContainer = null;
  private ProtocolServer myProtocolServer = null;
  
  public ProtocolFacade(Properties aProperties){
    ProtocolFactory theFactory = new ProtocolFactory(aProperties);
    myContainer = new ProtocolContainer(theFactory);
  }
  
  public ProtocolFacade(long anExchangeDelay, boolean isPersist, String aPeerId, String aKeyDir, String aUserName){
    Properties theProperties = new Properties();
    theProperties.setProperty( "routingprotocol.exchangedelay", Long.toString( anExchangeDelay));
    theProperties.setProperty("routingprotocol.persist", Boolean.toString( isPersist));
    theProperties.setProperty("peerid", aPeerId);
    theProperties.setProperty("key.location", aKeyDir);
    theProperties.setProperty("key.user", aUserName);
    ProtocolFactory theFactory = new ProtocolFactory(theProperties);
    myContainer = new ProtocolContainer(theFactory);
  }
  
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
  
  public void sendMessage(MultiPeerMessage aMessage) throws ProtocolException{
    ((MultiPeerMessageProtocol)myContainer.getProtocol( MultiPeerMessageProtocol.ID )).sendMessage( aMessage );
  }
  
  public void sendEnctryptedMessage(Message aMessage) throws MessageException, ProtocolException{
    aMessage.addMessageIndicator( MessageIndicator.TO_BE_ENCRYPTED );
    ((MessageProtocol)myContainer.getProtocol( MessageProtocol.ID )).sendMessage( aMessage );
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
  
  public void addUserInfoListener(iUserInfoListener aListener) throws ProtocolException{
    ((UserInfoProtocol)myContainer.getProtocol( UserInfoProtocol.ID )).addUserInfoListener( aListener );
  }
  
  public Map< String, UserInfo > getUserInfo() throws ProtocolException{
   return ((UserInfoProtocol)myContainer.getProtocol( UserInfoProtocol.ID )).getUserInfo();
  }
  
  public void setUserInfoProvider(iUserInfoProvider aUserInfoProvider) throws ProtocolException{
    ((UserInfoProtocol)myContainer.getProtocol( UserInfoProtocol.ID )).setUserInfoProvider( aUserInfoProvider );
  }
  
  public void start(int aNumberOfThreads){
    if(myProtocolServer == null){
      myProtocolServer = new ProtocolServer(myContainer, RoutingProtocol.START_PORT, aNumberOfThreads);
    } 
    
    if(!myProtocolServer.isStarted()){
      myProtocolServer.start();
    }
  }
  
  public void stop(){
    if(myProtocolServer != null){
      myProtocolServer.stop();
    }
  }
  
}
