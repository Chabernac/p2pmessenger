package chabernac.p2p.web;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.UUID;

import chabernac.comet.CometEvent;
import chabernac.comet.EndPoint;
import chabernac.protocol.routing.AbstractPeer;
import chabernac.protocol.routing.IndirectReachablePeer;
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
    return sendMessage( aMessage, aPeer, aTimeout );
  }
  
  @Override
  public String send(String aMessage, WebPeer aPeer, int aTimeout) throws IOException {
    return sendMessage( aMessage, aPeer, aTimeout );
  }
  
  private String sendMessage(String aMessage, AbstractPeer aPeer, int aTimeout) throws IOException{
    if(!myEndPoints.containsKey( aPeer.getPeerId() )) throw new IOException("Can not send a message to peer '" + aPeer.getPeerId() + "' because there is no endpoint for it");

    try {
      EndPoint theEndPoint = myEndPoints.get( aPeer.getPeerId() );
      UUID theUID = UUID.randomUUID();
      CometEvent theCometEvent = new CometEvent(theUID.toString(), aMessage);
      theEndPoint.setEvent( theCometEvent );
      return theCometEvent.getOutput(5000).replaceAll("\\{plus\\}", "+");
    } catch ( Exception e ) {
      throw new IOException("An exception occured while sending message to endpoint", e );
    }  
  }

  @Override
  public String send(String aMessage, IndirectReachablePeer aIndirectReachablePeer, int aTimeoutInSeconds) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setPeerId(String aPeerId) {
    // TODO Auto-generated method stub
    
  }


}
