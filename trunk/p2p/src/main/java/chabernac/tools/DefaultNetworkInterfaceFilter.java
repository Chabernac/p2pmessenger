/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.tools;

import java.net.NetworkInterface;

public class DefaultNetworkInterfaceFilter implements iNetworkInterfaceFilter {

  @Override
  public boolean isMatchingInterface( NetworkInterface anInterface ) {
    return true;
  }

}
