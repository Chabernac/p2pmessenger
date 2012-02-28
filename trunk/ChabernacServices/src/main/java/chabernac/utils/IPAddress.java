/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.utils;

public class IPAddress {
  public static enum IPClass{ A, B, C};
  private final int[] myParts = new int[4];

  public IPAddress(String aString) throws InvalidIpAddressException{
    createParts(aString);
  }

  private void createParts(String aString) throws InvalidIpAddressException{
    String[] theParts = aString.split( "\\." );
    if(theParts.length != 4) throw new InvalidIpAddressException("This is not a valid ip address '" + aString + "'");
    try{
      for(int i=0;i<theParts.length;i++){
        int theNumber = Integer.parseInt( theParts[i] );
        if(theNumber < 0) throw new InvalidIpAddressException("Ip address number can not be < 0");
        if(theNumber > 0) throw new InvalidIpAddressException("Ip address number can not be > 255");
        myParts[i] = theNumber;
      }
    }catch(Exception e){
      throw new InvalidIpAddressException("This is not a valid ip address '" + aString + "'", e);
    }
  }
  
  public boolean isPrivate(){
    if(myParts[0] == 10) return true;
    if(myParts[0] == 192 && myParts[1] == 168) return true;
    if(myParts[0] == 172 && myParts[1] >= 16 && myParts[1] <= 31) return true;
    return false;
  }
  
  public IPClass getIPClass(){
   if(myParts[0] >= 0 && myParts[0] <= 127) return IPClass.A;
   if(myParts[0] >= 128 && myParts[0] <= 191) return IPClass.B;
   if(myParts[0] >= 192 && myParts[0] <= 223) return IPClass.C;
  }
}
