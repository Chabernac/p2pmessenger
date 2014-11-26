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

import org.apache.log4j.Logger;

import chabernac.io.iCommunicationInterface;

//TODO find a way to move this class to chabernac.io without causing problems with deserialization on other systems
//which do not have the last version
public class SimpleNetworkInterface implements Serializable, iCommunicationInterface{
    private static final Logger LOGGER = Logger.getLogger(SimpleNetworkInterface.class);
  private static final long serialVersionUID = -2887291844821748090L;
  
  private final List<String> myIp;
  private final String myMACAddress;
  private final String myName;
  private final boolean isLoopBack;
  
  public SimpleNetworkInterface ( String aName, boolean isLoopBack, byte[] aMacAddress, String... anIp ) {
    super();
    myIp = Arrays.asList( anIp );
    myMACAddress = getMACString( aMacAddress );
    myName = aName;
    this.isLoopBack = isLoopBack;
  }
  
  public SimpleNetworkInterface ( String aName, boolean isLoopBack, String aMacAddress , String...anIp ) {
    super();
    myIp = Arrays.asList( anIp );
    myMACAddress = aMacAddress;
    myName = aName;
    this.isLoopBack = isLoopBack;
  }
  
  public static SimpleNetworkInterface createFromIpList(String aName, String... anIpList){
    return new SimpleNetworkInterface( aName, false, (String)null, anIpList );
  }
  
  public static SimpleNetworkInterface createForLoopBack(String aName){
    return new SimpleNetworkInterface( aName, true, (String)null, "127.0.0.1/8" );
  }

  public String getName() {
    return myName;
  }

  public List<String> getIp() {
    return myIp;
  }

  public static String getMACString(byte[] aMacAddress){
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
        if(getMACAddress().equalsIgnoreCase( theInterface.getMACAddress() )){
            LOGGER.debug("Found same mac address for both hosts '" + getMACAddress() + "'");
        }
      return getMACAddress().equalsIgnoreCase( theInterface.getMACAddress() );
    } else {
      //compare ip addresses
      for(String theIp : myIp){
        if(theInterface.getIp().contains( theIp )){
            LOGGER.debug( "Found same ip address: '" + theIp + "' in both hosts" );
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
    if(myMACAddress != null) return myMACAddress;
    return myName;
  }

  public boolean isLoopBack() {
    return isLoopBack;
  }
  
}
