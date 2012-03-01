/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2p.io;

import java.io.IOException;

import chabernac.io.iSocketSender;
import chabernac.protocol.ProtocolServer;
import chabernac.protocol.routing.PeerMessage;
import chabernac.protocol.routing.PeerSenderReply;
import chabernac.protocol.routing.SocketPeer;
import chabernac.tools.PropertyMap;
import chabernac.tools.SimpleNetworkInterface;

public class PeerToPeerSplitSender {
  private final iSocketSender mySocketSender;

  public PeerToPeerSplitSender ( iSocketSender aSocketSender ) {
    super();
    mySocketSender = aSocketSender;
  }
  
  public PeerSenderReply sendMessageTo(PeerMessage aPeerMessage, SocketPeer aPeer, String aMessage, int aTimeoutInSeconds) throws IOException {
    for(SimpleNetworkInterface theHost : aPeer.getHosts()){
      for(String theIp : theHost.getIp()){
        String theResponse = mySocketSender.send( aPeer.getPeerId(), theIp, aPeer.getPort(), aMessage);
        PropertyMap theProperties = new PropertyMap();
        theProperties.put( ProtocolServer.SOCKET, mySocketSender.getSocket( aPeer.getPeerId() ) );
        return new PeerSenderReply( theResponse,  theProperties);
      }
    }
    throw new IOException("Could not send message to '" + aPeer.getPeerId() + "'");
  }
}
