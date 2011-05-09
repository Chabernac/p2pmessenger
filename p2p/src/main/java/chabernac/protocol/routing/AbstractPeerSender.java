/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.io.IOException;

public abstract class AbstractPeerSender implements iPeerSender {

  @Override
  public final String send( AbstractPeer aPeer, String aMessage ) throws IOException {
   return send(aPeer, aMessage, 5);
  }

  @Override
  public final String send( AbstractPeer aTo, String aMessage, int aTimeout ) throws IOException {
    if(aMessage.length() < 3) throw new IOException("Can not send message which has no protocol");
    String theProtocol = aMessage.substring( 0, 3 );
    if(!aTo.isProtocolSupported( theProtocol )) throw new IOException("The protocol '" + theProtocol + "' is not supported by peer '" + aTo.getPeerId() + "'");

    return doSend(aTo, aMessage, aTimeout);
  }

  protected abstract String doSend( AbstractPeer aTo, String aMessage, int aTimeout ) throws IOException;
}
