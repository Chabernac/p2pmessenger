/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.tools;

import java.net.InetAddress;

public class LoggingIPListener implements iIPListener {

  @Override
  public void IPRemoved( InetAddress anAddress ) {
    System.out.println("address removed: " + anAddress);
  }

  @Override
  public void newIPBound( InetAddress anAddress ) {
    System.out.println("address detected: " + anAddress);
  }

}
