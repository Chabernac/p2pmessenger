/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2p.io;

import java.io.IOException;

import chabernac.io.iSocketSender;
import chabernac.protocol.routing.PeerMessage;
import chabernac.protocol.routing.SocketPeer;
import chabernac.tools.SimpleNetworkInterface;

public class PeerToPeerSender2 {
  private final iSocketSender mySocketSender;

  public PeerToPeerSender2 ( iSocketSender aSocketSender ) {
    super();
    mySocketSender = aSocketSender;
  }
  
  public String sendMessageTo(PeerMessage aPeerMessage, SocketPeer aPeer, String aMessage, int aTimeoutInSeconds) throws IOException {
    for(SimpleNetworkInterface theHost : aPeer.getHosts()){
      for(String theIp : theHost.getIp()){
        return mySocketSender.send( aPeer.getPeerId(), theIp, aPeer.getPort(), aMessage);
      }
    }
    throw new IOException("Could not send message to '" + aPeer.getPeerId() + "'");
  }
}
