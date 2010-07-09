/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

public class NrOfPeersSmallerThenCondition implements iCondition {
  private RoutingTable myRoutingTable = null;
  private int myMaxNrOfPeers = 1;

  
  public NrOfPeersSmallerThenCondition ( RoutingTable anRoutingTable , int anMaxNrOfPeers ) {
    super();
    myRoutingTable = anRoutingTable;
    myMaxNrOfPeers = anMaxNrOfPeers;
  }


  @Override
  public boolean isConditionFullFilled() {
    return myRoutingTable.getNrOfReachablePeers() <= myMaxNrOfPeers;
  }

}
