/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.net.URL;

import chabernac.utils.NamedRunnable;

public class ScanWebSystem extends NamedRunnable{
  private RoutingProtocol myRoutingProtocol = null;
  private final URL myEndPoint;

  public ScanWebSystem ( RoutingProtocol aProtocol, URL anEndPoint ){
    myRoutingProtocol = aProtocol;
    myEndPoint = anEndPoint;
  }


  @Override
  public void doRun() {
    WebPeer thePeer = new WebPeer(myEndPoint);
    if(myRoutingProtocol.getRoutingProtocolMonitor() != null) myRoutingProtocol.getRoutingProtocolMonitor().scanStarted( thePeer );
    boolean result = myRoutingProtocol.contactPeer( thePeer, null, true );
    if(result && myRoutingProtocol.getRoutingProtocolMonitor() != null) myRoutingProtocol.getRoutingProtocolMonitor().peerFoundWithScan( thePeer );
  }
}
