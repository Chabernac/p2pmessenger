/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol;

import java.io.IOException;

import javax.activation.DataSource;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.TTCCLayout;

import chabernac.protocol.echo.EchoProtocol;
import chabernac.protocol.encryption.EncryptionException;
import chabernac.protocol.encryption.EncryptionProtocol;
import chabernac.protocol.filetransfer.FileTransferProtocol;
import chabernac.protocol.infoexchange.InfoExchangeProtocol;
import chabernac.protocol.infoexchange.InfoObject;
import chabernac.protocol.list.ListProtocol;
import chabernac.protocol.message.MessageProtocol;
import chabernac.protocol.message.MultiPeerMessageProtocol;
import chabernac.protocol.ping.PingProtocol;
import chabernac.protocol.pipe.PipeProtocol;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.userinfo.DefaultUserInfoProvider;
import chabernac.protocol.userinfo.UserInfoProtocol;
import chabernac.protocol.userinfo.iUserInfoProvider;
import chabernac.protocol.version.Version;
import chabernac.protocol.version.VersionProtocol;
import chabernac.tools.PropertyMap;

public class ProtocolFactory implements iProtocolFactory{
  private PropertyMap myProtocolProperties = null;
  private static Logger LOGGER = Logger.getLogger(ProtocolFactory.class);

  public ProtocolFactory(PropertyMap aProtocolProperties){
    myProtocolProperties = aProtocolProperties;
  }

  @Override
  public Protocol createProtocol( String aProtocolId ) throws ProtocolException {
    Protocol theProtocol = createProt( aProtocolId );
    try {
      setupLogging( theProtocol );
    } catch ( IOException e ) {
      LOGGER.error( "Could not setup logging for protocol '" + theProtocol.getClass().getName() + "'" );
    }
    return theProtocol;
  }

  private Protocol createProt( String aProtocolId ) throws ProtocolException {
    if(RoutingProtocol.ID.equalsIgnoreCase( aProtocolId )) {
      long theExchangeDelay = Long.parseLong( myProtocolProperties.getProperty( "routingprotocol.exchangedelay",  "300").toString() );
      boolean isPersistRoutingTable = Boolean.parseBoolean(myProtocolProperties.getProperty( "routingprotocol.persist",  "true").toString());
      boolean isStopWhenAlreadyRunning = Boolean.parseBoolean(myProtocolProperties.getProperty( "routingprotocol.stopwhenalreadyrunning",  "false").toString());
      String thePeerId = myProtocolProperties.getProperty( "peerid", "" ).toString();
      DataSource theSuperNodesDataSource = (DataSource)myProtocolProperties.getProperty( "routingprotocol.supernodes", null);
      return new RoutingProtocol(thePeerId, theExchangeDelay, isPersistRoutingTable, theSuperNodesDataSource, isStopWhenAlreadyRunning);
    }

    if(EchoProtocol.ID.equalsIgnoreCase( aProtocolId )) {
      return new EchoProtocol();
    }

    if(PipeProtocol.ID.equalsIgnoreCase( aProtocolId )) {
      int theNumberOfSocketsAllowd = Integer.parseInt(myProtocolProperties.getProperty( "pipeprotocol.sockets", "5" ).toString());
      return new PipeProtocol(theNumberOfSocketsAllowd);
    }

    if(FileTransferProtocol.ID.equalsIgnoreCase( aProtocolId )) {
      return new FileTransferProtocol();
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
      Version theLocalVersion = (Version)myProtocolProperties.get( "chabernac.protocol.version" );
      if(theLocalVersion == null) theLocalVersion = new Version("0.0.1");
      VersionProtocol theProtocol = new VersionProtocol(theLocalVersion);
      return theProtocol;
    }
    
    if(InfoExchangeProtocol.ID.equalsIgnoreCase( aProtocolId )) {
      InfoObject theInfoObject = new InfoObject();
      InfoExchangeProtocol<InfoObject> theProtocol = new InfoExchangeProtocol< InfoObject >(theInfoObject);
      return theProtocol;
    }

    throw new ProtocolException("The protocol with id '" + aProtocolId + "' is not known");
  }
  
  private void setupLogging(Protocol aProtocol) throws IOException{
    Logger theLogger = Logger.getLogger( aProtocol.getClass().getName() );
    DailyRollingFileAppender theFileAppender = new DailyRollingFileAppender(new TTCCLayout("dd-MM-yyyy HH:mm:ss SSS") ,aProtocol.getClass().getName(), "'.'yyyy-MM-dd'.log'" );
    theLogger.addAppender( theFileAppender );
  }

}
