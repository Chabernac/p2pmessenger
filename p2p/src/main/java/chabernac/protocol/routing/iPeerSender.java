package chabernac.protocol.routing;

import java.io.IOException;

public interface iPeerSender {
  public String send(String aMessage, Peer aPeer, int aTimeout) throws IOException;
}
