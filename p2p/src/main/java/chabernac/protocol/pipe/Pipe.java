/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.pipe;

import chabernac.io.SocketProxy;
import chabernac.protocol.routing.SocketPeer;

public class Pipe {
  private SocketPeer myPeer = null;
  private SocketProxy mySocket = null;
  private String myPipeDescription = "";
  
  public Pipe ( SocketPeer anPeer ) {
    super();
    myPeer = anPeer;
  }

  public SocketProxy getSocket() {
    return mySocket;
  }

  public void setSocket( SocketProxy anSocket ) {
    mySocket = anSocket;
  }

  public SocketPeer getPeer() {
    return myPeer;
  }

  public String getPipeDescription() {
    return myPipeDescription;
  }

  public void setPipeDescription(String anPipeDescription) {
    myPipeDescription = anPipeDescription;
  }
}
