package chabernac.protocol.routing;

import java.io.IOException;

public interface iPeerSender {
  public PeerSenderReply send(AbstractPeer aPeer, String aMessage) throws IOException;
  public PeerSenderReply send(AbstractPeer aPeer, String aMessage, int aTimeout) throws IOException;
}
