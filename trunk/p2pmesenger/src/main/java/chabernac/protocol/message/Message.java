/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import java.util.ArrayList;
import java.util.List;

import chabernac.protocol.routing.Peer;

public class Message {
  private Peer mySource = null;
  private Peer myDestination = null;
  private String myMessage = null;
  private List<MessageIndicator> myIndicators = null;
  private boolean isProtocolMessage = false;
  
  //only when the byte array is small we can use it to transport bytes trough the network.
  //because the message is reformed to xml the xml will be many times bigger as the byte array
  //so transporting large byte array's is not a good practice.
  private byte[] myBytes = null;
  
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
  
  public boolean containsIndicator(MessageIndicator anIndicator){
    if(myIndicators == null){
      return false;
    }
    return myIndicators.contains( anIndicator );
  }
  public boolean isProtocolMessage() {
    return isProtocolMessage;
  }
  public void setProtocolMessage( boolean anProtocolMessage ) {
    isProtocolMessage = anProtocolMessage;
  }
} 
