/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol;

import java.util.Map;
import java.util.Properties;

import chabernac.protocol.echo.EchoProtocol;
import chabernac.protocol.encryption.EncryptionException;
import chabernac.protocol.encryption.EncryptionProtocol;
import chabernac.protocol.filetransfer.FileTransferProtocol;
import chabernac.protocol.list.ListProtocol;
import chabernac.protocol.message.MessageProtocol;
import chabernac.protocol.message.MultiPeerMessageProtocol;
import chabernac.protocol.ping.PingProtocol;
import chabernac.protocol.pipe.PipeProtocol;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.userinfo.DefaultUserInfoProvider;
import chabernac.protocol.userinfo.UserInfoProtocol;
import chabernac.protocol.userinfo.iUserInfoProvider;
import chabernac.tools.PropertyMap;

public class ProtocolFactory implements iProtocolFactory{
  private PropertyMap myProtocolProperties = null;

  public ProtocolFactory(PropertyMap aProtocolProperties){
    myProtocolProperties = aProtocolProperties;
  }


  @Override
  public Protocol createProtocol( String aProtocolId ) throws ProtocolException {
    if(RoutingProtocol.ID.equalsIgnoreCase( aProtocolId )) {
      long theExchangeDelay = Long.parseLong( myProtocolProperties.getProperty( "routingprotocol.exchangedelay",  "300").toString() );
      boolean isPersistRoutingTable = Boolean.parseBoolean(myProtocolProperties.getProperty( "routingprotocol.persist",  "true").toString());
      String thePeerId = myProtocolProperties.getProperty( "peerid", "" ).toString();
      return new RoutingProtocol(thePeerId, theExchangeDelay, isPersistRoutingTable);
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

    throw new ProtocolException("The protocol with id '" + aProtocolId + "' is not known");
  }

}
