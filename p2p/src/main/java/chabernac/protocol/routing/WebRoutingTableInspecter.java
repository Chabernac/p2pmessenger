/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.util.Map;

/**
 * 
 * This class will
 * <br><br>
 * <u><i>Version History</i></u>
 * <pre>
 * v2011.10.0 14-apr-2011 - DGCH804 - initial release
 *
 * </pre>
 *
 * @version v2011.10.0      14-apr-2011
 * @author <a href="mailto:guy.chauliac@axa.be"> Guy Chauliac </a>
 */

public class WebRoutingTableInspecter implements iRoutingTableInspector {
  private final Map<String, String> myPeerExternalIpLink;
  private final SessionData mySessionData;
  
  public WebRoutingTableInspecter( SessionData aSessionData, Map<String, String> aPeerExternalIpLink ) {
    super();
    myPeerExternalIpLink = aPeerExternalIpLink;
    mySessionData = aSessionData;
  }

  @Override
  public RoutingTable inspectRoutingTable( String aSessionId, RoutingTable aRoutingTable ) {
    if(!mySessionData.containsSession( aSessionId )) return aRoutingTable;
    
    String theIPRequestor = mySessionData.getProperty( aSessionId, "requestor.ip" );
    
    //now lets create a new routing table and replace all the peers with dummy peers which have not the same ip
    //the ip of the requestor is the exposed ip.  All requests coming from the same domain will have the same external ip
    
    
    RoutingTable theTable = new RoutingTable( aRoutingTable.getLocalPeerId() );
    
    for(RoutingTableEntry theEntry : theTable.getEntries()){
      if(theEntry.getPeer() instanceof SocketPeer){
        SocketPeer thePeer = (SocketPeer)theEntry.getPeer();
        String theExposedIp = myPeerExternalIpLink.get( thePeer.getPeerId() );
        
        if(theExposedIp.equals( theIPRequestor )){
          theTable.addRoutingTableEntry(  theEntry );
        } else {
          IndirectReachablePeer theNewPeer = new IndirectReachablePeer(thePeer.getPeerId());
          RoutingTableEntry theNewEntry = new RoutingTableEntry( theNewPeer, theEntry.getHopDistance(), theEntry.getGateway(), theEntry.getLastOnlineTime());
          theTable.addRoutingTableEntry(theNewEntry);
        }
      } else {
        theTable.addRoutingTableEntry(theEntry);
      }
    }
    
    return theTable;
  }

}
