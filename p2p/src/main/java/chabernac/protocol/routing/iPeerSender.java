package chabernac.protocol.routing;

import java.io.IOException;

public interface iPeerSender {
  public String send(AbstractPeer aPeer, String aMessage) throws IOException;
  public String send(AbstractPeer aPeer, String aMessage, int aTimeout) throws IOException;
}
