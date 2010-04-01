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
  private int[] myCurrent = null;
  private int myNumberOfAddresses;
  private int myCounter = 0;
  
  public InetAddresIterator(InetAddress aStartAddress, int aNumberOfAddresses){
    myInetAddress = aStartAddress;
    String[] theParts = myInetAddress.getHostAddress().split( "\\." );
    myParts = new int[theParts.length];
    myCurrent = new int[theParts.length];
    for(int i=0;i<theParts.length;i++){
      myParts[i] = Integer.parseInt(  theParts[i] );
      myCurrent[i] = Integer.parseInt( theParts[i] );
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
    int theIndex = 3;
    while((myCurrent[theIndex] = nextNumberAtIndex( theIndex )) == myParts[theIndex] && theIndex >= 0){
      theIndex--;
    }
    return getInetAddress();
  }
  
  private String getInetAddress() {
    return myCurrent[0] + "." + myCurrent[1] + "." + myCurrent[2] + "." + myCurrent[3];
  }

  private int nextNumberAtIndex(int anIndex){
    int theDiff = (myCurrent[anIndex] - myParts[anIndex]) * 2;
    if(theDiff >= 0) theDiff++;
    else theDiff--;
    theDiff *= -1;
    int theNewNumber = myCurrent[anIndex] + theDiff;
    if(theNewNumber == 0 || theNewNumber == 256){
      theNewNumber = myParts[anIndex];
    }
    return theNewNumber;
  }

  @Override
  public void remove() {
    // TODO Auto-generated method stub
    
  }

}
