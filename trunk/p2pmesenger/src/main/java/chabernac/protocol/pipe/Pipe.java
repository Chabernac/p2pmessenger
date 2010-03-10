/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.pipe;

import java.net.Socket;

import chabernac.protocol.routing.Peer;

public class Pipe {
  private Peer myPeer = null;
  private Socket mySocket = null;
  
  public Pipe ( Peer anPeer ) {
    super();
    myPeer = anPeer;
  }

  public Socket getSocket() {
    return mySocket;
  }

  public void setSocket( Socket anSocket ) {
    mySocket = anSocket;
  }

  public Peer getPeer() {
    return myPeer;
  }
  
}
