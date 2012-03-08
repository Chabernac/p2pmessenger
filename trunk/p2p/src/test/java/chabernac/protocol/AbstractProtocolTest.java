/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import chabernac.io.StreamSplittingServer;
import chabernac.p2p.settings.P2PSettings;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.SocketRoutingTableInspector;
import chabernac.tools.PropertyMap;

public abstract class AbstractProtocolTest extends TestCase {
  protected static final int SLEEP_AFTER_SCAN = 2000;
  
  protected static enum ServerMode{SOCKET, STREAM_SPLITTING};
  protected ServerMode myServerMode = ServerMode.STREAM_SPLITTING;
  
  public void setUp() throws Exception{
    P2PSettings.getInstance().getSocketPool().cleanUp();
  }
  
  public ProtocolContainer getProtocolContainer(long anExchangeDelay, boolean isPersist, String aPeerId){
    PropertyMap theProperties = new PropertyMap();
    theProperties.setProperty( "routingprotocol.exchangedelay", Long.toString( anExchangeDelay));
    theProperties.setProperty("routingprotocol.persist", Boolean.toString( isPersist));
    
    if(aPeerId != null) theProperties.setProperty("peerid", aPeerId);
    ProtocolFactory theFactory = new ProtocolFactory(theProperties);
    return new ProtocolContainer(theFactory);
  }
  
  public ProtocolContainer getProtocolContainer(long anExchangeDelay, boolean isPersist, String aPeerId, String... aSuperNodes){
    PropertyMap theProperties = new PropertyMap();
    theProperties.setProperty( "routingprotocol.exchangedelay", Long.toString( anExchangeDelay));
    theProperties.setProperty("routingprotocol.persist", Boolean.toString( isPersist));
    
    List<String> theSuperNodes = new ArrayList<String>();
    for(String theSuperNode : aSuperNodes) theSuperNodes.add(theSuperNode);
    theProperties.setProperty("routingprotocol.supernodes", theSuperNodes );
    
    if(aPeerId != null) theProperties.setProperty("peerid", aPeerId);
    ProtocolFactory theFactory = new ProtocolFactory(theProperties);
    return new ProtocolContainer(theFactory);
  }
  
  protected iP2PServer getP2PServer(ProtocolContainer aProtocolContainer, int aStartPort) throws ProtocolException{
    if(myServerMode == ServerMode.SOCKET){
      return new ProtocolServer(aProtocolContainer, aStartPort); 
    } else if(myServerMode == ServerMode.STREAM_SPLITTING){
      RoutingProtocol theRoutingProtocol = (RoutingProtocol)aProtocolContainer.getProtocol( RoutingProtocol.ID );
      
      InputOutputProtocolAdapter theAdaptor = new InputOutputProtocolAdapter( aProtocolContainer );
      StreamSplittingServer theServer = new StreamSplittingServer( 
          theAdaptor, aStartPort, true, theRoutingProtocol.getLocalPeerId() );
      theAdaptor.setStreamSplittingServer( theServer );
      theServer.addListener( new StreamSplittingServerListener( aProtocolContainer ) );
      theRoutingProtocol.setRoutingTableInspector( new SocketRoutingTableInspector(aProtocolContainer.getSessionData() ) );
      return new P2PServerSplittingServerAdapter( theServer );
    }
    throw new ProtocolException("Could not create p2p server");
  }

}
