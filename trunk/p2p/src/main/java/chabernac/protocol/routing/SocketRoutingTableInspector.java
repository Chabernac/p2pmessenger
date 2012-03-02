/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import chabernac.protocol.ProtocolServer;
import chabernac.tools.SimpleNetworkInterface;
import chabernac.utils.IPAddress;
import chabernac.utils.InvalidIpAddressException;

public class SocketRoutingTableInspector implements iRoutingTableInspector {
  private static Logger LOGGER = Logger.getLogger(SocketRoutingTableInspector.class);
  
  private final SessionData mySessionData;
  
  public SocketRoutingTableInspector( SessionData aSessionData ) {
    super();
    mySessionData = aSessionData;
  }

  @Override
  public RoutingTable inspectRoutingTable( String aSessionId, RoutingTable aRoutingTable ) {
    String theRemoteIpAddress = (String)mySessionData.getProperty( aSessionId, ProtocolServer.REMOTE_IP );
    if(theRemoteIpAddress == null) return aRoutingTable;
    if("127.0.0.1".equals( theRemoteIpAddress )) return aRoutingTable;
    
    IPAddress theRemoteIp;
    try{
      theRemoteIp = new IPAddress(theRemoteIpAddress);
    }catch(InvalidIpAddressException e){
      LOGGER.debug("Could not parse ip addres for remote ip", e );
      return aRoutingTable;
    }
    
    RoutingTable theNewRoutingTable = new RoutingTable( aRoutingTable.getLocalPeerId() );
    
    for(RoutingTableEntry theEntry : aRoutingTable.getEntries()){
      if(theEntry.getPeer() instanceof SocketPeer){
        SocketPeer thePeer = (SocketPeer)theEntry.getPeer();
        
        List<SimpleNetworkInterface> theHosts = thePeer.getHosts();
        List<SimpleNetworkInterface> theNewHosts = new ArrayList<SimpleNetworkInterface>();
        for(SimpleNetworkInterface theHost : theHosts){
          String[] theIps = theHost.getIp();
          List<String> theNewIps = new ArrayList<String>();
          for(String theIp: theIps){
            try{
            IPAddress theIPAddress = new IPAddress(theIp);
            if(theIPAddress.isOnSameNetwork( theRemoteIp )){
              theNewIps.add( theIp );
            }
            }catch(Exception e){
              LOGGER.debug( "Could not parse ip address", e );
            }
          }
          if(theNewIps.size() > 0){
            theNewHosts.add( new SimpleNetworkInterface( theHost.getMACAddress(), theNewIps.toArray(new String[]{}) ));
          }
        }
        AbstractPeer theNewPeer;
        if(theNewHosts.size() > 0){
          theNewPeer = new SocketPeer( thePeer, theNewHosts );
        } else {
          theNewPeer = new IndirectReachablePeer( thePeer );
        }
        RoutingTableEntry theNewEntry = new RoutingTableEntry( theNewPeer, theEntry.getHopDistance(), theEntry.getGateway(), theEntry.getLastOnlineTime());
        theNewRoutingTable.addRoutingTableEntry(theNewEntry);
      } else {
        theNewRoutingTable.addRoutingTableEntry( theEntry );
      }
    }
    return theNewRoutingTable;
  }

}
