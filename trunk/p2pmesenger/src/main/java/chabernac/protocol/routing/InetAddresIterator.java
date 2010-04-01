/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class InetAddresIterator implements Iterator< String > {
  private InetAddress myInetAddress = null;
  private int[] myParts = null;
  private int[] myCurrentUp = null;
  private int[] myCurrentDown = null;
  private boolean isUp = true;
  private int myNumberOfAddresses;
  private int myCounter = 0;
  
  public InetAddresIterator(InetAddress aStartAddress, int aNumberOfAddresses){
    myInetAddress = aStartAddress;
    String[] theParts = myInetAddress.getHostAddress().split( "\\." );
    myParts = new int[theParts.length];
    myCurrentUp = new int[theParts.length];
    myCurrentDown = new int[theParts.length];
    for(int i=0;i<theParts.length;i++){
      myParts[i] = Integer.parseInt(  theParts[i] );
      myCurrentUp[i] = Integer.parseInt( theParts[i] );
      myCurrentDown[i] = Integer.parseInt( theParts[i] );
    }
    myNumberOfAddresses = aNumberOfAddresses;
  }

  @Override
  public boolean hasNext() {
    return myCounter < myNumberOfAddresses;
  }

  @Override
  public String next() throws NoSuchElementException{
    myCounter++;
    
    String theAddress = null;
    if(isUp){
      myCurrentUp = next( myCurrentUp );
      theAddress = getInetAddress( myCurrentUp );
    } else {
      myCurrentDown = previous( myCurrentDown );
      theAddress = getInetAddress( myCurrentDown );
    }
    isUp = !isUp;
    return theAddress;
  }
  
  private String getInetAddress(int[] anAddress) {
    return anAddress[0] + "." + anAddress[1] + "." + anAddress[2] + "." + anAddress[3];
  }

  
  
  public int[] next(int[] anAddress){
    anAddress[3]++;
    if(anAddress[3] == 256){
      anAddress[3] = 1;
      anAddress[2]++;
    }
    if(anAddress[2] == 256){
      anAddress[2] = 0;
      anAddress[1]++;
    }
    if(anAddress[1] == 256){
      anAddress[1] = 0;
      anAddress[0]++;
    }
    return anAddress;
  }
  
  public int[] previous(int[] anAddress){
    anAddress[3]--;
    if(anAddress[3] == 0){
      anAddress[3] = 255;
      anAddress[2]--;
    }
    if(anAddress[2] == 0){
      anAddress[2] = 255;
      anAddress[1]--;
    }
    if(anAddress[1] == 0){
      anAddress[1] = 255;
      anAddress[0]--;
    }
    return anAddress;
  }
  @Override
  public void remove() {
    // TODO Auto-generated method stub
    
  }

}
