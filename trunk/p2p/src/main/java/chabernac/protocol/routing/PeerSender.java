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
  
  protected String doSend(PeerMessage aMessage, int aTimeoutInSeconds) throws IOException{
    try{
      AbstractPeer theFrom = myRoutingTable.getEntryForLocalPeer().getPeer();
      AbstractPeer theTo = aMessage.getPeer();
      
//      if(theFrom.getPeerId() == aTo.getPeerId()) throw new IOException("You are sending a message to you self");

      if(theTo instanceof WebPeer){
        return myPeerToWebSender.sendMessageTo( theFrom, (WebPeer)theTo, aMessage.getMessage(), aTimeoutInSeconds );
      } else if(theFrom instanceof WebPeer){
        return myWebToPeerSender.sendMessageTo( (WebPeer)theFrom, theTo, aMessage.getMessage(), aTimeoutInSeconds ); 
      } else if(theTo instanceof SocketPeer){
        return myPeerToPeerSender.sendMessageTo(aMessage, (SocketPeer)theTo, aMessage.getMessage(), aTimeoutInSeconds );
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
}
