/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol;

import java.net.URL;

import chabernac.io.StreamSplittingServer;
import chabernac.protocol.routing.JVMPeerSender;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;

public class P2PServerFactory {
  public static enum ServerMode{SOCKET, WEB, SPLITTING_SOCKET, BOTH};

  private static void addJVMPeerSender(ProtocolContainer aProtocolContainer) throws P2PServerFactoryException{
    try{
      RoutingProtocol theRoutingProtocol = (RoutingProtocol)aProtocolContainer.getProtocol( RoutingProtocol.ID );
      RoutingTable theRoutingTable = theRoutingProtocol.getRoutingTable(); 
      JVMPeerSender.getInstance().addPeerProtocol(theRoutingTable.getLocalPeerId(), aProtocolContainer);
    }catch(ProtocolException e){
      throw new P2PServerFactoryException("Could not add jvm peer sender", e);
    }
  }

  public static iP2PServer createWebServer(ProtocolContainer aProtocolContainer, URL aWebURL, int aWebPort, Integer anAJPPort) throws P2PServerFactoryException{
    if(aWebURL == null) throw new P2PServerFactoryException( "Must set a web url before starting" );
    ProtocolWebServer theProtocolWebServer = new ProtocolWebServer( aProtocolContainer, aWebPort, aWebURL );
    if(anAJPPort != null) theProtocolWebServer.setAJPPort( anAJPPort );
    addJVMPeerSender(aProtocolContainer);
    return theProtocolWebServer;
  }

  public static iP2PServer createSocketServer(ProtocolContainer aProtocolContainer, ServerMode aServerMode) throws P2PServerFactoryException{
    RoutingProtocol.determinePorts( aServerMode );

    try{
      RoutingProtocol theRoutingProtocol = (RoutingProtocol)aProtocolContainer.getProtocol( RoutingProtocol.ID );
//      theRoutingProtocol.setRoutingTableInspector( new SocketRoutingTableInspector(aProtocolContainer.getSessionData() ) );
      
      addJVMPeerSender(aProtocolContainer);

      if(aServerMode == ServerMode.SOCKET){
        return new ProtocolServer(aProtocolContainer, RoutingProtocol.START_PORT, true); 
      } else if(aServerMode == ServerMode.SPLITTING_SOCKET || aServerMode == ServerMode.BOTH){
        InputOutputProtocolAdapter theAdaptor = new InputOutputProtocolAdapter( aProtocolContainer );
        StreamSplittingServer theServer = new StreamSplittingServer(theAdaptor, RoutingProtocol.START_PORT, true, theRoutingProtocol.getLocalPeerId() );
        theAdaptor.setStreamSplittingServer( theServer );
        theServer.addListener( new StreamSplittingServerListener( aProtocolContainer ) );
        return new P2PServerSplittingServerAdapter( theServer );
      }
    }catch(ProtocolException e){
      throw new P2PServerFactoryException("Could not create p2p server", e);
    }
    throw new P2PServerFactoryException("Could not create p2p server");
  }
}
