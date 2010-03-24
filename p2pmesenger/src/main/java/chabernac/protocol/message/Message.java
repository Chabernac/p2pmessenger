/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import chabernac.protocol.routing.Peer;

public class Message {
  private Peer mySource = null;
  private Peer myDestination = null;
  private String myMessage = null;
  
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
} 
