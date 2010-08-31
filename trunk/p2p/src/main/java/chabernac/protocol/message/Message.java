/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import chabernac.protocol.routing.Peer;

public class Message {
  private Peer mySource = null;
  private Peer myDestination = null;
  private String myMessage = null;
  private Map<String, String> myHeaders = null;
  private List<MessageIndicator> myIndicators = null;
  private boolean isProtocolMessage = false;
  private UUID myMessageId = UUID.randomUUID();
  
  //only when the byte array is small we can use it to transport bytes trough the network.
  //because the message is reformed to xml the xml will be many times bigger as the byte array
  //so transporting large byte array's is not a good practice.
  private byte[] myBytes = null;
  
  private int myTTL = 8;
  
  public Peer getSource() {
    return mySource;
  }
  public void setSource( Peer anSource ) {
    mySource = anSource;
  }
  public Peer getDestination() {
    return myDestination;
  }
  public void setDestination( Peer anDestination ) {
    myDestination = anDestination;
  }
  public String getMessage() {
    return myMessage;
  }
  public void setMessage( String anMessage ) {
    myMessage = anMessage;
  }
  public byte[] getBytes() {
    return myBytes;
  }
  public void setBytes( byte[] anBytes ) {
    myBytes = anBytes;
  }
  public void addMessageIndicator(MessageIndicator anIndicator){
    if(myIndicators == null){
      myIndicators = new ArrayList< MessageIndicator >();
    }
    myIndicators.add( anIndicator );
  }
  
  public void removeMessageIndicator(MessageIndicator anIndicator){
    if(myIndicators != null){
      myIndicators.remove( anIndicator );
    }
  }
  
  public boolean containsIndicator(MessageIndicator anIndicator){
    if(myIndicators == null){
      return false;
    }
    return myIndicators.contains( anIndicator );
  }
  
  public void addHeader(String aHeader, String aContent){
    synchronized(this){
      if(myHeaders == null) myHeaders = new HashMap< String, String >();
    }
    myHeaders.put( aHeader, aContent );
  }
  
  public void removeHeader(String aHeader){
    myHeaders.remove( aHeader );
  }
  
  public String getHeader(String aHeader){
    return myHeaders.get(aHeader);
  }
                          
  public Map< String, String > getHeaders() {
    return myHeaders;
  }
  public void setHeaders( Map< String, String > anHeaders ) {
    myHeaders = anHeaders;
  }
  public boolean isProtocolMessage() {
    return isProtocolMessage;
  }
  public void setProtocolMessage( boolean anProtocolMessage ) {
    isProtocolMessage = anProtocolMessage;
  }
  public List< MessageIndicator > getIndicators() {
    return myIndicators;
  }
  public void setIndicators( List< MessageIndicator > anIndicators ) {
    myIndicators = anIndicators;
  }
  public int getTTL() {
    return myTTL;
  }
  public void setTTL( int anTtl ) {
    myTTL = anTtl;
  }
  
  public void decreaseTTL(){
    if(myTTL > 0){
      myTTL--;
    }
  }
  public boolean isEndOfTTL() {
    return myTTL == 0;
  }
  public UUID getMessageId() {
    return myMessageId;
  }
  public void setMessageId( UUID anMessageId ) {
    myMessageId = anMessageId;
  }
} 
