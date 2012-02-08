package chabernac.protocol.routing;

import java.io.IOException;

import chabernac.io.iSocketSender;
import chabernac.p2p.io.PeerToPeerSender;
import chabernac.p2p.io.PeerToPeerSplitSender;
import chabernac.p2p.io.PeerToWebSender;
import chabernac.p2p.io.WebToPeerSender;

public class PeerSender extends AbstractPeerSender{
  private final RoutingTable myRoutingTable;

  private final WebToPeerSender myWebToPeerSender = new WebToPeerSender();
  private final PeerToPeerSender myPeerToPeerSender = new PeerToPeerSender();
  private final PeerToPeerSplitSender myPeerToPeerSplitSender;
  private final PeerToWebSender myPeerToWebSender = new PeerToWebSender();

  public PeerSender(iSocketSender aSocketSender, RoutingTable aRoutingTable){
    myRoutingTable = aRoutingTable;
    myPeerToPeerSplitSender = aSocketSender == null ? null :  new PeerToPeerSplitSender(aSocketSender);
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
        SocketPeer theSocketPeer = (SocketPeer)theTo;
        //only if both ends support stream splitting then use it
        if(myPeerToPeerSplitSender != null &&  theSocketPeer.isStreamSplittingSupported()) return myPeerToPeerSplitSender.sendMessageTo(aMessage, (SocketPeer)theTo, aMessage.getMessage(), aTimeoutInSeconds );
        else return myPeerToPeerSender.sendMessageTo(aMessage, (SocketPeer)theTo, aMessage.getMessage(), aTimeoutInSeconds );
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
