/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.util.List;

import org.apache.log4j.Logger;

import chabernac.io.SimpleNetworkInterface;
import chabernac.utils.NamedRunnable;

public class ScanSystem extends NamedRunnable{
  private static Logger LOGGER = Logger.getLogger(ScanSystem.class);
  
  private String[] myHosts;
  private int myPort;
  private List<String> myUnreachablePeers = null;
  private RoutingProtocol myRoutingProtocol = null;
  //condition which must be fullfilled to make the scan happen
  //if not fullfilled skip the scan
  private iCondition myCondition = null;

  public ScanSystem ( RoutingProtocol aProtocol, int anPort, String... aHosts ){
    this(aProtocol, anPort, null, aHosts);
  }

  public ScanSystem ( RoutingProtocol aProtocol, int anPort, List<String> anUnreachablePeers, String... aHosts) {
    super();
    myHosts = aHosts;
    myPort = anPort;
    myUnreachablePeers = anUnreachablePeers;
    myRoutingProtocol = aProtocol;
  }

  @Override
  public void doRun() {
    if(myCondition == null || myCondition.isConditionFullFilled()){
      LOGGER.debug( "Scanning system '" + myHosts + "': '" + myPort + "'" );
      SocketPeer thePeer = new SocketPeer(null, SimpleNetworkInterface.createFromIpList(null, myHosts), myPort);
      if(myRoutingProtocol.getRoutingProtocolMonitor() != null) myRoutingProtocol.getRoutingProtocolMonitor().scanStarted( thePeer );
      boolean result = myRoutingProtocol.contactPeer( thePeer, myUnreachablePeers, true );
      if(result && myRoutingProtocol.getRoutingProtocolMonitor() != null) myRoutingProtocol.getRoutingProtocolMonitor().peerFoundWithScan( thePeer );
    }
  }

  public iCondition getCondition() {
    return myCondition;
  }

  public void setCondition( iCondition anCondition ) {
    myCondition = anCondition;
  }
}
