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
  private int myMessageTimeoutInSeconds = 5;
  
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
    myMessageId = UUID.randomUUID();
  }
  public String getMessage() {
    return myMessage;
  }
  public void setMessage( String anMessage ) {
    if(isLocked) throw new ConcurrentModificationException("Can not modify message when it is locked");
    myMessage = anMessage;
    myMessageId = UUID.randomUUID();
  }
  public byte[] getBytes() {
    return myBytes;
  }
  public void setBytes( byte[] anBytes ) {
    if(isLocked) throw new ConcurrentModificationException("Can not modify message when it is locked");
    myBytes = anBytes;
    myMessageId = UUID.randomUUID();
  }
  public void addMessageIndicator(MessageIndicator anIndicator){
    if(isLocked) throw new ConcurrentModificationException("Can not modify message when it is locked");
    if(myIndicators == null){
      myIndicators = new ArrayList< MessageIndicator >();
    }
    myIndicators.add( anIndicator );
    myMessageId = UUID.randomUUID();
  }
  
  public void removeMessageIndicator(MessageIndicator anIndicator){
    if(isLocked) throw new ConcurrentModificationException("Can not modify message when it is locked");
    if(myIndicators != null){
      myIndicators.remove( anIndicator );
    }
    myMessageId = UUID.randomUUID();
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
    myMessageId = UUID.randomUUID();
  }
  
  public void removeHeader(String aHeader){
    if(isLocked) throw new ConcurrentModificationException("Can not modify message when it is locked");
    myHeaders.remove( aHeader );
    myMessageId = UUID.randomUUID();
  }
  
  public String getHeader(String aHeader){
    return myHeaders.get(aHeader);
  }
  
  public boolean containsHeader(String aHeader){
    return myHeaders.containsKey(aHeader);
  }
                          
  public Map< String, String > getHeaders() {
    return Collections.unmodifiableMap(myHeaders == null ? new HashMap<String, String>() : myHeaders );
  }
  public void setHeaders( Map< String, String > anHeaders ) {
    if(isLocked) throw new ConcurrentModificationException("Can not modify message when it is locked");
    myHeaders = anHeaders;
    myMessageId = UUID.randomUUID();
  }
  public boolean isProtocolMessage() {
    return isProtocolMessage;
  }
  public void setProtocolMessage( boolean anProtocolMessage ) {
    if(isLocked) throw new ConcurrentModificationException("Can not modify message when it is locked");
    isProtocolMessage = anProtocolMessage;
    myMessageId = UUID.randomUUID();
  }
  public List< MessageIndicator > getIndicators() {
    return Collections.unmodifiableList( myIndicators == null ? new ArrayList<MessageIndicator>() : myIndicators );
  }
  public void setIndicators( List< MessageIndicator > anIndicators ) {
    if(isLocked) throw new ConcurrentModificationException("Can not modify message when it is locked");
    myIndicators = anIndicators;
    myMessageId = UUID.randomUUID();
  }
  public int getTTL() {
    return myTTL;
  }
  public void setTTL( int anTtl ) {
    if(isLocked) throw new ConcurrentModificationException("Can not modify message when it is locked");
    myTTL = anTtl;
    myMessageId = UUID.randomUUID();
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
    myMessageId = UUID.randomUUID();
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
  public int getMessageTimeoutInSeconds() {
    return myMessageTimeoutInSeconds;
  }
  public void setMessageTimeoutInSeconds( int anMessageTimeoutInSeconds ) {
    myMessageTimeoutInSeconds = anMessageTimeoutInSeconds;
  }
  
  public Message copy(){
    Message theMessage = new Message();
    theMessage.setSource(getSource());
    theMessage.setDestination(getDestination());
    if(getHeaders() != null) theMessage.setHeaders(new HashMap<String, String>(getHeaders()));
    if(getIndicators() != null) theMessage.setIndicators(new ArrayList<MessageIndicator>(getIndicators()));
    theMessage.setMessageTimeoutInSeconds(getMessageTimeoutInSeconds());
    theMessage.setMessage(getMessage());
    theMessage.setProtocolMessage(isProtocolMessage);
    theMessage.setTTL(getTTL());
    theMessage.setBytes(getBytes());
    return theMessage;
  }
}