/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.net.URL;
import java.util.Map;

import org.apache.log4j.Logger;

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
  private static Logger LOGGER = Logger.getLogger(WebRoutingTableInspecter.class);
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

    String theIPRequestor = (String)mySessionData.getProperty( aSessionId, "requestor.ip" );
    String theURLRequestor = (String)mySessionData.getProperty( aSessionId, "requestor.url" );

    LOGGER.debug("Inspecting routing table in session '" + aSessionId + "' for peer with remote ip '" + theIPRequestor + "' and requesting url '" + theURLRequestor + "'");

    boolean hasIPRequestor = theIPRequestor != null && !"".equals( theIPRequestor );
    boolean hasURLRequestor = theURLRequestor != null && !"".equals( theURLRequestor );

    //Now lets create a new routing table and replace all the peers with dummy peers which have not the same ip as the requestor
    //The ip of the requestor is the exposed ip.  All requests coming from the same domain will have the same external ip
    RoutingTable theTable = new RoutingTable( aRoutingTable.getLocalPeerId() );

    for(RoutingTableEntry theEntry : aRoutingTable.getEntries()){
      if(hasIPRequestor && theEntry.getPeer() instanceof SocketPeer){
        SocketPeer thePeer = (SocketPeer)theEntry.getPeer();

        String theExposedIp = myPeerExternalIpLink.get( thePeer.getPeerId() );

        LOGGER.debug("Inspecting routing table entry of peer '" + thePeer.getPeerId() + "' which has external ip '" + theExposedIp + "'");

        if(theIPRequestor.equals(theExposedIp)){
          LOGGER.debug("The ip of the requesting peer '" + theIPRequestor + "' matches the ip of the inspected routing table entry '" + theExposedIp + "' adding the entry unchanged");
          theTable.addRoutingTableEntry( theEntry );
        } else {
          LOGGER.debug("The ip of the requesting peer '" + theIPRequestor + "' does not match the ip of the inspected routing table entry '" + theExposedIp + "' adding indirect reachable peer");
          IndirectReachablePeer theNewPeer = new IndirectReachablePeer(thePeer);
          RoutingTableEntry theNewEntry = new RoutingTableEntry( theNewPeer, theEntry.getHopDistance(), theEntry.getGateway(), theEntry.getLastOnlineTime());
          theTable.addRoutingTableEntry(theNewEntry);
        }
      } else if(hasURLRequestor && theEntry.getHopDistance() == 0 && theEntry.getPeer() instanceof WebPeer){
        try{
          WebPeer theLocalWebPeer = (WebPeer)theEntry.getPeer();
          WebPeer theWebPeer = new WebPeer( new URL(theURLRequestor), theLocalWebPeer);
          RoutingTableEntry theNewEntry = new RoutingTableEntry( theWebPeer, theEntry.getHopDistance(), theWebPeer, theEntry.getLastOnlineTime());
          theTable.addRoutingTableEntry( theNewEntry );
        }catch(Exception e){
          LOGGER.error("Could not create copy of local web peer, e");
          theTable.addRoutingTableEntry( theEntry );
        }
      } else {
        theTable.addRoutingTableEntry(theEntry);
      }
    }

    return theTable;
  }

}
