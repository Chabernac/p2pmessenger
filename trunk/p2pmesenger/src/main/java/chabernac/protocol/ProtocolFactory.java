/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol;

import java.lang.reflect.Constructor;
import java.util.Properties;

import chabernac.protocol.echo.EchoProtocol;
import chabernac.protocol.filetransfer.FileTransferProtocol;
import chabernac.protocol.keyexchange.KeyExchangeProtocol;
import chabernac.protocol.keyexchange.PublicPrivateKeyEnctryptionFactory;
import chabernac.protocol.keyexchange.iPublicPrivateKeyEncryptionFactory;
import chabernac.protocol.list.ListProtocol;
import chabernac.protocol.message.MessageProtocol;
import chabernac.protocol.ping.PingProtocol;
import chabernac.protocol.pipe.PipeProtocol;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.userinfo.UserInfoProtocol;
import chabernac.protocol.userinfo.iUserInfoProvider;

public class ProtocolFactory implements iProtocolFactory{
  private Properties myProtocolProperties = null;
  
  public ProtocolFactory(Properties aProtocolProperties){
    myProtocolProperties = aProtocolProperties;
  }
  

  @Override
  public Protocol createProtocol( String aProtocolId ) throws ProtocolException {
    if(RoutingProtocol.ID.equalsIgnoreCase( aProtocolId )) {
      long theExchangeDelay = Long.parseLong( myProtocolProperties.getProperty( "routingprotocol.exchangedelay",  "300") );
      boolean isPersistRoutingTable = Boolean.parseBoolean(myProtocolProperties.getProperty( "routingprotocol.persist",  "true"));
      String thePeerId = myProtocolProperties.getProperty( "peerid", "" );
      return new RoutingProtocol(thePeerId, theExchangeDelay, isPersistRoutingTable);
    }
    
    if(EchoProtocol.ID.equalsIgnoreCase( aProtocolId )) {
      return new EchoProtocol();
    }
    
    if(PipeProtocol.ID.equalsIgnoreCase( aProtocolId )) {
      int theNumberOfSocketsAllowd = Integer.parseInt(myProtocolProperties.getProperty( "pipeprotocol.sockets", "5" ));
      return new PipeProtocol(theNumberOfSocketsAllowd);
    }
    
    if(FileTransferProtocol.ID.equalsIgnoreCase( aProtocolId )) {
      return new FileTransferProtocol();
    }
    
    if(KeyExchangeProtocol.ID.equalsIgnoreCase( aProtocolId )) {
      try{
        Class<iPublicPrivateKeyEncryptionFactory> thePublicPrivateKeyEnctryptionFactory = (Class<iPublicPrivateKeyEncryptionFactory>)Class.forName( myProtocolProperties.getProperty( "publicprivatekey.factory", PublicPrivateKeyEnctryptionFactory.class.getName()));
        
        Constructor<iPublicPrivateKeyEncryptionFactory> theConstructor = (Constructor< iPublicPrivateKeyEncryptionFactory >)thePublicPrivateKeyEnctryptionFactory.getConstructor( Properties.class );
        
        iPublicPrivateKeyEncryptionFactory theFactory = theConstructor.newInstance(new Object[]{myProtocolProperties});
        return new KeyExchangeProtocol(theFactory.createEncryption());
      }catch(Exception e){
        throw new ProtocolException("KeyExchangeProtocol could not be instantiated", e);
      }
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
    
    if(UserInfoProtocol.ID.equalsIgnoreCase( aProtocolId )) {
      try{
        String theUserInfoProvider = myProtocolProperties.getProperty( "chabernac.protocol.userinfo.iUserInfoProvider", "chabernac.protocol.userinfo.DefaultUserInfoProvider" );
        Class theClass = Class.forName( theUserInfoProvider );
        iUserInfoProvider theUserInfoProviderInstance = (iUserInfoProvider)theClass.newInstance();
        return new UserInfoProtocol(theUserInfoProviderInstance);
      }catch(Exception e){
        throw new ProtocolException("UserInfoProtocol could not be instantiated", e);
      }
    }
    
    throw new ProtocolException("The protocol with id '" + aProtocolId + "' is not known");
  }

}
