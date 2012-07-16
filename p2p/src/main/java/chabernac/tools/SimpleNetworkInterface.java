/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.tools;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

public class SimpleNetworkInterface implements Serializable, iNetworkInterface{
  private static final long serialVersionUID = -2887291844821748090L;
  
  private final List<String> myIp;
  private final String myMACAddress;
  
  public SimpleNetworkInterface ( byte[] aMacAddress, String... anIp ) {
    super();
    myIp = Arrays.asList( anIp );
    myMACAddress = getMACString( aMacAddress );
  }
  
  public SimpleNetworkInterface ( String aMacAddress , String...anIp ) {
    super();
    myIp = Arrays.asList( anIp );
    myMACAddress = aMacAddress;
  }
  
  public static SimpleNetworkInterface createFromIpList(String... anIpList){
    return new SimpleNetworkInterface( (String)null, anIpList );
  }

  public List<String> getIp() {
    return myIp;
  }

  private static String getMACString(byte[] aMacAddress){
    if(aMacAddress == null) return null;
    Formatter theFormatter = new Formatter();
    for(int i=0;i<aMacAddress.length;i++){
      theFormatter.format(Locale.getDefault(), "%02X%s", aMacAddress[i], i+1 < aMacAddress.length ? "-" : "" ); 
    }
    return theFormatter.toString();
  }
  
  public String getMACAddress(){
    return myMACAddress;
  }
  
  public String toString(){
    StringBuilder theBuilder = new StringBuilder();
    theBuilder.append( myMACAddress );
    
    theBuilder.append ( ": ");
    for(String theIp: myIp){
      theBuilder.append(theIp);
      theBuilder.append(", ");
    }
    return theBuilder.toString();
  }
  
  public boolean equals(Object anObject){
    if(!(anObject instanceof SimpleNetworkInterface)) return false;
    SimpleNetworkInterface theInterface = (SimpleNetworkInterface)anObject;
    if(myMACAddress != null && theInterface.getMACAddress() != null){
      return getMACAddress().equalsIgnoreCase( theInterface.getMACAddress() );
    } else {
      //compare ip addresses
      for(String theIp : myIp){
        if(theInterface.getIp().contains( theIp )){
          return true;
        }
      }
    }
    return false;
  }
  
  public int hashCode(){
    if(myMACAddress != null){
      return myMACAddress.hashCode();
    }
    return super.hashCode();
  }

  @Override
  public String getId() {
    return myMACAddress;
  }
  
}
