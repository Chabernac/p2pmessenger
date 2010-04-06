/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class ScanSystem implements Runnable{
  private static Logger LOGGER = Logger.getLogger(ScanSystem.class);
  
  private List<String> myHosts;
  private int myPort;
  private List<String> myUnreachablePeers = null;
  private RoutingProtocol myRoutingProtocol = null;
  //condition which must be fullfilled to make the scan happen
  //if not fullfilled skip the scan
  private iCondition myCondition = null;

  public ScanSystem ( RoutingProtocol aProtocol, String aHosts, int anPort ){
    this(aProtocol, aHosts, anPort, null);
  }

  public ScanSystem ( RoutingProtocol aProtocol, String aHosts, int anPort, List<String> anUnreachablePeers) {
    super();
    List<String> theList = new ArrayList< String >();
    theList.add(aHosts);
    myHosts = theList;
    myPort = anPort;
    myUnreachablePeers = anUnreachablePeers;
    myRoutingProtocol = aProtocol;
  }

  public ScanSystem ( RoutingProtocol aProtocol, List<String> aHosts, int anPort, List<String> anUnreachablePeers) {
    super();
    myHosts = aHosts;
    myPort = anPort;
    myUnreachablePeers = anUnreachablePeers;
    myRoutingProtocol = aProtocol;
  }

  @Override
  public void run() {
    if(myCondition == null || myCondition.isConditionFullFilled()){
      LOGGER.debug( "Scanning system '" + myHosts + "': '" + myPort + "'" );
      Peer thePeer = new Peer(null, myHosts, myPort);
      if(myRoutingProtocol.getRoutingProtocolMonitor() != null) myRoutingProtocol.getRoutingProtocolMonitor().scanStarted( thePeer );
      boolean result = myRoutingProtocol.contactPeer( thePeer, myUnreachablePeers );
      if(result && myRoutingProtocol.getRoutingProtocolMonitor() != null) myRoutingProtocol.getRoutingProtocolMonitor().peerFoundWithScan( thePeer );
      //let's just sleep for a while to make the system free up the socket resource.
//      try {
//        Thread.sleep( 10000 );
//      } catch ( InterruptedException e ) {
//      }
    }
  }

  public iCondition getCondition() {
    return myCondition;
  }

  public void setCondition( iCondition anCondition ) {
    myCondition = anCondition;
  }
}
