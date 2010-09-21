package chabernac.p2p;

import java.io.IOException;
import java.util.Map;

import chabernac.comet.EndPoint;
import chabernac.protocol.routing.SocketPeer;
import chabernac.protocol.routing.WebPeer;
import chabernac.protocol.routing.iPeerSender;

public class WebPeerSender implements iPeerSender {
  protected Map<String, EndPoint> myEndPoints;
  
  public WebPeerSender(Map<String, EndPoint> anEndPoints) {
    super();
    myEndPoints = anEndPoints;
  }

  @Override
  public String send(String aMessage, SocketPeer aPeer, int aTimeout) throws IOException {
    
  }

  @Override
  public String send(String aMessage, WebPeer aPeer, int aTimeout) throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

}
