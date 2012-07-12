/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.utils;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPAddress {
  public static enum IPClass{ A, B, C, UNKNOWN};
  private final int[] myParts = new int[4];
  private int myNetworkPrefixLength = -1;
  private final static Pattern PATTERN = Pattern.compile( "(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})(?:/(\\d{1,2}))?" );

  public static IPAddress getLocalIPAddress() throws InvalidIpAddressException{
    try{
      InetAddress localHost = Inet4Address.getLocalHost();
      NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
      return new IPAddress(localHost.getHostAddress() + "/" + networkInterface.getInterfaceAddresses().get(0).getNetworkPrefixLength());
    }catch(Exception e){
      throw new InvalidIpAddressException("Could not determine local ip address", e);
    }
  }

  public static boolean isIpAddress(String aString){
    return PATTERN.matcher( aString ).matches();
  }

  public IPAddress(String aString) throws InvalidIpAddressException{
    createParts(aString);
    determineNetworkPrefixLength();
  }

  private void createParts(String aString) throws InvalidIpAddressException{
    Matcher theMatcher = PATTERN.matcher( aString );
    if(!theMatcher.matches()) throw new InvalidIpAddressException("This is not a valid ip address '" + aString + "'");

    for(int i=0;i<4;i++)
    {
      int thePart = Integer.parseInt(theMatcher.group( i+1 ));
      if(thePart < 0) throw new InvalidIpAddressException("Number can not be < 0");
      if(thePart > 255) throw new InvalidIpAddressException("Number can not be > 255");
      myParts[i] = thePart;
    }

    String theNetworkPrefixLength = theMatcher.group(5);
    if(theNetworkPrefixLength != null){
      myNetworkPrefixLength = Integer.parseInt(theNetworkPrefixLength);
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
    return IPClass.UNKNOWN;
  }

  private void determineNetworkPrefixLength(){
    if(myNetworkPrefixLength == -1){
      IPClass theClass = getIPClass();
      if(theClass == IPClass.A) myNetworkPrefixLength = 8;
      else if(theClass == IPClass.B) myNetworkPrefixLength = 16;
      else if(theClass == IPClass.C) myNetworkPrefixLength = 24;
    }
  }

  public int getNetworkPrefixLength(){
    return myNetworkPrefixLength;
  }

  public String getBitRepresentation(){
    String theBits = "";
    for(int thePart : myParts){
      theBits += toBitRepresentation( thePart );
    }
    return theBits;
  }

  private String toBitRepresentation(int aNumber){
    String theBits = Integer.toBinaryString( aNumber );
    while(theBits.length() < 8) theBits = "0" + theBits;
    return theBits;
  }

  public String getNetworkBitRepresentation(){
    return getBitRepresentation().substring( 0, getNetworkPrefixLength() );
  }

  public String getHostBitRepresentation(){
    return getBitRepresentation().substring( getNetworkPrefixLength() );
  }

  public String getIpAddressWithNetworkPrefixLength(){
    return getIPAddressOnly() + "/" + getNetworkPrefixLength();
  }

  public String getIPAddressOnly(){
    return myParts[0] + "." + myParts[1] + "." + myParts[2] + "." + myParts[3];
  }

  public boolean isOnSameNetwork(IPAddress anAddress){
    return getNetworkBitRepresentation().equals( anAddress.getNetworkBitRepresentation() );
  }

  public static IPAddress getIPAddressForLocalIP(String anIp) throws IOException{
    Enumeration<NetworkInterface> theInterfaces = NetworkInterface.getNetworkInterfaces();
    while(theInterfaces.hasMoreElements()){
      NetworkInterface theInterface = theInterfaces.nextElement();
      List<InterfaceAddress> theAddresses = theInterface.getInterfaceAddresses();
      for(InterfaceAddress theAddress : theAddresses){
        if(theAddress.getAddress().getHostAddress().equals(anIp)){
          try {
            return new IPAddress(anIp + "/" + theAddress.getNetworkPrefixLength());
          } catch (InvalidIpAddressException e) {
            throw new IOException("Could not create ip  address with '" + anIp + "/" + theAddress.getNetworkPrefixLength() + "'", e);
          }
        }
      }
    }
    throw new IOException("No network interface found for given ip '" + anIp + "'");
  }
}
