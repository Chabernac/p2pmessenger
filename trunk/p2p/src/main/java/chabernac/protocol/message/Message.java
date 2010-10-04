/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import chabernac.protocol.routing.AbstractPeer;

public class Message implements Serializable{
  private static final long serialVersionUID = -8695593402222208110L;
  private AbstractPeer mySource = null;
  private AbstractPeer myDestination = null;
  private String myMessage = null;
  private Map<String, String> myHeaders = null;
  private List<MessageIndicator> myIndicators = null;
  private boolean isProtocolMessage = false;
  private UUID myMessageId = UUID.randomUUID();
  private long myCreationTime = System.currentTimeMillis();
  private transient boolean isLocked = false;
  
  private static int TTL = 8;
  
  //only when the byte array is small we can use it to transport bytes trough the network.
  //because the message is reformed to xml the xml will be many times bigger as the byte array
  //so transporting large byte array's is not a good practice.
  private byte[] myBytes = null;
  
  private int myTTL = TTL;
  
  public AbstractPeer getSource() {
    return mySource;
  }
  public void setSource( AbstractPeer anSource ) {
    mySource = anSource;
  }
  public AbstractPeer getDestination() {
    return myDestination;
  }
  public void setDestination( AbstractPeer anDestination ) {
    if(isLocked) throw new ConcurrentModificationException("Can not modify message when it is locked");
    myDestination = anDestination;
  }
  public String getMessage() {
    return myMessage;
  }
  public void setMessage( String anMessage ) {
    if(isLocked) throw new ConcurrentModificationException("Can not modify message when it is locked");
    myMessage = anMessage;
  }
  public byte[] getBytes() {
    return myBytes;
  }
  public void setBytes( byte[] anBytes ) {
    if(isLocked) throw new ConcurrentModificationException("Can not modify message when it is locked");
    myBytes = anBytes;
  }
  public void addMessageIndicator(MessageIndicator anIndicator){
    if(isLocked) throw new ConcurrentModificationException("Can not modify message when it is locked");
    if(myIndicators == null){
      myIndicators = new ArrayList< MessageIndicator >();
    }
    myIndicators.add( anIndicator );
  }
  
  public void removeMessageIndicator(MessageIndicator anIndicator){
    if(isLocked) throw new ConcurrentModificationException("Can not modify message when it is locked");
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
    if(isLocked) throw new ConcurrentModificationException("Can not modify message when it is locked");
    synchronized(this){
      if(myHeaders == null) myHeaders = new HashMap< String, String >();
    }
    myHeaders.put( aHeader, aContent );
  }
  
  public void removeHeader(String aHeader){
    if(isLocked) throw new ConcurrentModificationException("Can not modify message when it is locked");
    myHeaders.remove( aHeader );
  }
  
  public String getHeader(String aHeader){
    return myHeaders.get(aHeader);
  }
                          
  public Map< String, String > getHeaders() {
    return myHeaders;
  }
  public void setHeaders( Map< String, String > anHeaders ) {
    if(isLocked) throw new ConcurrentModificationException("Can not modify message when it is locked");
    myHeaders = anHeaders;
  }
  public boolean isProtocolMessage() {
    return isProtocolMessage;
  }
  public void setProtocolMessage( boolean anProtocolMessage ) {
    if(isLocked) throw new ConcurrentModificationException("Can not modify message when it is locked");
    isProtocolMessage = anProtocolMessage;
  }
  public List< MessageIndicator > getIndicators() {
    return Collections.unmodifiableList( myIndicators );
  }
  public void setIndicators( List< MessageIndicator > anIndicators ) {
    if(isLocked) throw new ConcurrentModificationException("Can not modify message when it is locked");
    myIndicators = anIndicators;
  }
  public int getTTL() {
    return myTTL;
  }
  public void setTTL( int anTtl ) {
    if(isLocked) throw new ConcurrentModificationException("Can not modify message when it is locked");
    myTTL = anTtl;
  }
  
  public void decreaseTTL(){
    if(isLocked) throw new ConcurrentModificationException("Can not modify message when it is locked");
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
    if(isLocked) throw new ConcurrentModificationException("Can not modify message when it is locked");
    myMessageId = anMessageId;
  }
  public long getCreationTime() {
    return myCreationTime;
  }
  public void setCreationTime( long anCreationTime ) {
    if(isLocked) throw new ConcurrentModificationException("Can not modify message when it is locked");
    myCreationTime = anCreationTime;
  }
  
  public void resetTTL(){
    if(isLocked) throw new ConcurrentModificationException("Can not modify message when it is locked");
    myTTL = TTL;
  }
  public boolean isLocked() {
    return isLocked;
  }
  public void setLocked( boolean anLocked ) {
    isLocked = anLocked;
  }
}