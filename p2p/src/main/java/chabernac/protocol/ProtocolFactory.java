/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol;

import java.util.Collection;

import chabernac.protocol.application.ApplicationProtocol;
import chabernac.protocol.asyncfiletransfer.AsyncFileTransferProtocol;
import chabernac.protocol.asyncfiletransfer.iAsyncFileTransferHandler;
import chabernac.protocol.cam.CamProtocol;
import chabernac.protocol.echo.EchoProtocol;
import chabernac.protocol.encryption.EncryptionException;
import chabernac.protocol.encryption.EncryptionProtocol;
import chabernac.protocol.filetransfer.FileHandlerDialogDispatcher;
import chabernac.protocol.filetransfer.FileTransferProtocol;
import chabernac.protocol.filetransfer.iFileHandler;
import chabernac.protocol.infoexchange.InfoExchangeProtocol;
import chabernac.protocol.infoexchange.InfoObject;
import chabernac.protocol.list.ListProtocol;
import chabernac.protocol.message.AsyncMessageProcotol;
import chabernac.protocol.message.MessageProtocol;
import chabernac.protocol.message.MultiPeerMessageProtocol;
import chabernac.protocol.packet.AsyncTransferProtocol;
import chabernac.protocol.packet.PacketProtocol;
import chabernac.protocol.ping.PingProtocol;
import chabernac.protocol.pipe.PipeProtocol;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.WebPeerProtocol;
import chabernac.protocol.stacktrace.ProcessProtocol;
import chabernac.protocol.userinfo.DefaultUserInfoProvider;
import chabernac.protocol.userinfo.UserInfoProtocol;
import chabernac.protocol.userinfo.iUserInfoProvider;
import chabernac.protocol.version.Version;
import chabernac.protocol.version.VersionProtocol;
import chabernac.tools.PropertyMap;

public class ProtocolFactory implements iProtocolFactory{
  private PropertyMap myProtocolProperties = null;
  
  public ProtocolFactory(PropertyMap aProtocolProperties){
    myProtocolProperties = aProtocolProperties;
  }

  @Override
  public Protocol createProtocol( String aProtocolId ) throws ProtocolException {
    return createProt( aProtocolId );
  }

  private Protocol createProt( String aProtocolId ) throws ProtocolException {
    if(RoutingProtocol.ID.equalsIgnoreCase( aProtocolId )) {
      long theExchangeDelay = Long.parseLong( myProtocolProperties.getProperty( "routingprotocol.exchangedelay",  "300").toString() );
      boolean isPersistRoutingTable = Boolean.parseBoolean(myProtocolProperties.getProperty( "routingprotocol.persist",  "true").toString());
      boolean isStopWhenAlreadyRunning = Boolean.parseBoolean(myProtocolProperties.getProperty( "routingprotocol.stopwhenalreadyrunning",  "false").toString());
      String thePeerId = myProtocolProperties.getProperty( "peerid", "" ).toString();
      Collection<String> theSuperNodes = (Collection<String>)myProtocolProperties.getProperty( "routingprotocol.supernodes", null);
      return new RoutingProtocol(thePeerId, 
          theExchangeDelay, 
          isPersistRoutingTable, 
          theSuperNodes, 
          isStopWhenAlreadyRunning, 
          myProtocolProperties.getProperty( "routingprotocol.channel",  "default").toString()); 
    }
    
    if(ApplicationProtocol.ID.equalsIgnoreCase( aProtocolId )) {
      return new ApplicationProtocol();
    }

    if(EchoProtocol.ID.equalsIgnoreCase( aProtocolId )) {
      return new EchoProtocol();
    }

    if(PipeProtocol.ID.equalsIgnoreCase( aProtocolId )) {
      int theNumberOfSocketsAllowd = Integer.parseInt(myProtocolProperties.getProperty( "pipeprotocol.sockets", "5" ).toString());
      return new PipeProtocol(theNumberOfSocketsAllowd);
    }

    if(FileTransferProtocol.ID.equalsIgnoreCase( aProtocolId )) {
      FileTransferProtocol theProtocol = new FileTransferProtocol();
      if(myProtocolProperties.containsKey( "chabernac.protocol.filetransfer.iFileHandler" )){
        theProtocol.setFileHandler( (iFileHandler )myProtocolProperties.getProperty( "chabernac.protocol.filetransfer.iFileHandler", new FileHandlerDialogDispatcher() ));
      }
      return theProtocol;
    }

    if(MessageProtocol.ID.equalsIgnoreCase( aProtocolId )) {
      return new MessageProtocol();
    }

    if(PingProtocol.ID.equalsIgnoreCase( aProtocolId )) {
      return new PingProtocol();
    }

    if(ListProtocol.ID.equalsIgnoreCase( aProtocolId )) {
      return new ListProtocol();
    }

    if(MultiPeerMessageProtocol.ID.equalsIgnoreCase( aProtocolId )) {
      return new MultiPeerMessageProtocol();
    }

    if(EncryptionProtocol.ID.equalsIgnoreCase( aProtocolId )) {
      try {
        return new EncryptionProtocol();
      } catch ( EncryptionException e ) {
        throw new ProtocolException("Encryption protocol could not be instantiated", e);
      }
    }

    if(UserInfoProtocol.ID.equalsIgnoreCase( aProtocolId )) {
      try{
        iUserInfoProvider theUserInfoProvider = (iUserInfoProvider)myProtocolProperties.getProperty( "chabernac.protocol.userinfo.iUserInfoProvider", new DefaultUserInfoProvider() );
        return new UserInfoProtocol(theUserInfoProvider);
      }catch(Exception e){
        throw new ProtocolException("UserInfoProtocol could not be instantiated", e);
      }
    }
    
    if(VersionProtocol.ID.equalsIgnoreCase( aProtocolId )) {
      Version theLocalVersion = (Version)myProtocolProperties.getProperty( "chabernac.protocol.version", new Version("0.0.1"));
      VersionProtocol theProtocol = new VersionProtocol(theLocalVersion);
      return theProtocol;
    }
    
    if(InfoExchangeProtocol.ID.equalsIgnoreCase( aProtocolId )) {
      InfoObject theInfoObject = (InfoObject)myProtocolProperties.getProperty( "chabernac.protocol.infoexchange.InfoObject",  new InfoObject());
      InfoExchangeProtocol<InfoObject> theProtocol = new InfoExchangeProtocol< InfoObject >(theInfoObject);
      return theProtocol;
    }
    
    if(WebPeerProtocol.ID.equalsIgnoreCase( aProtocolId )) {
      return new WebPeerProtocol();
    }
    
    if(ProcessProtocol.ID.equalsIgnoreCase( aProtocolId )) {
      return new ProcessProtocol();
    }
    
    if(AsyncMessageProcotol.ID.equalsIgnoreCase( aProtocolId )) {
      return new AsyncMessageProcotol();
    }
    
    if(AsyncFileTransferProtocol.ID.equalsIgnoreCase( aProtocolId )){
      AsyncFileTransferProtocol theProtocol = new AsyncFileTransferProtocol();
      if(myProtocolProperties.containsKey( "chabernac.protocol.filetransfer.iAsyncFileTransferHandler" )){
        theProtocol.setFileHandler( (iAsyncFileTransferHandler )myProtocolProperties.getProperty( "chabernac.protocol.filetransfer.iAsyncFileTransferHandler", null ));
      }
      return theProtocol;
    }
    
    if(PacketProtocol.ID.equalsIgnoreCase( aProtocolId )) {
      return new PacketProtocol();
    }
    
    if(CamProtocol.ID.equalsIgnoreCase( aProtocolId )) {
      return new CamProtocol();
    }
    
    if(AsyncTransferProtocol.ID.equalsIgnoreCase( aProtocolId )) {
      return new AsyncTransferProtocol( );
    }


    throw new ProtocolException("The protocol with id '" + aProtocolId + "' is not known");
  }
}
