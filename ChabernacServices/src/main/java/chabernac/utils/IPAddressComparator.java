/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.utils;

import java.util.Comparator;

public class IPAddressComparator implements Comparator<IPAddress> {
  
  private final IPAddress myLocalAddress;
  
  public IPAddressComparator()throws InvalidIpAddressException{
    this(IPAddress.getLocalIPAddress());
  }
  
  public IPAddressComparator(IPAddress aLocalAddress) {
    myLocalAddress = aLocalAddress;
  }

  @Override
  public int compare( IPAddress anAddress1, IPAddress anAddress2 ) {
    boolean theAddress1OnSamenetwork = myLocalAddress.isOnSameNetwork( anAddress1 );
    boolean theAddress2OnSamenetwork = myLocalAddress.isOnSameNetwork( anAddress2 );
    
   
    if(theAddress1OnSamenetwork == theAddress2OnSamenetwork) return 0;
    if(theAddress1OnSamenetwork) return -1;
    return 1;
  }

}
