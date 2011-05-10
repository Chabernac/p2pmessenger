package chabernac.protocol.routing;

import java.io.IOException;

import chabernac.p2p.io.PeerToPeerSender;
import chabernac.p2p.io.PeerToWebSender;
import chabernac.p2p.io.WebToPeerSender;

public class PeerSender extends AbstractPeerSender{
  private final RoutingTable myRoutingTable;

  private final WebToPeerSender myWebToPeerSender = new WebToPeerSender();
  private final PeerToPeerSender myPeerToPeerSender = new PeerToPeerSender();
  private final PeerToWebSender myPeerToWebSender = new PeerToWebSender();

  public PeerSender(RoutingTable aRoutingTable){
    myRoutingTable = aRoutingTable;
  }
  
  protected String doSend(AbstractPeer aTo, String aMessage, int aTimeoutInSeconds) throws IOException{
    try{
      AbstractPeer theFrom = myRoutingTable.getEntryForLocalPeer().getPeer();
      
      if(theFrom.getPeerId() == aTo.getPeerId()) throw new IOException("You are sending a message to you self");

      if(theFrom instanceof WebPeer){
        return myWebToPeerSender.sendMessageTo( (WebPeer)theFrom, aTo, aMessage, aTimeoutInSeconds ); 
      } else if(aTo instanceof WebPeer){
        return myPeerToWebSender.sendMessageTo( theFrom, (WebPeer)aTo, aMessage, aTimeoutInSeconds );
      } else if(aTo instanceof SocketPeer){
        return myPeerToPeerSender.sendMessageTo( (SocketPeer)aTo, aMessage, aTimeoutInSeconds );
      }
    }catch(Exception e){
      throw new IOException("Could not send message", e);
    }
    throw new IOException("Unsuported peer combination");
  }

  public WebToPeerSender getWebToPeerSender() {
    return myWebToPeerSender;
  }

  public PeerToPeerSender getPeerToPeerSender() {
    return myPeerToPeerSender;
  }

  public PeerToWebSender getPeerToWebSender() {
    return myPeerToWebSender;
  }

  public long getBytesReceived() {
    // TODO Auto-generated method stub
    return 0;
  }

  public long getBytesSend() {
    // TODO Auto-generated method stub
    return 0;
  }

  public long getInitTime() {
    // TODO Auto-generated method stub
    return 0;
  }
}
