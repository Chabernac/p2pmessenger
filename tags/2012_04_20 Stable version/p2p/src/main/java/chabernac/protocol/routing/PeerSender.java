package chabernac.protocol.routing;

import java.io.IOException;
import org.apache.log4j.Logger;

import chabernac.io.iSocketSender;
import chabernac.p2p.io.PeerToPeerSender;
import chabernac.p2p.io.PeerToPeerSplitSender;
import chabernac.p2p.io.PeerToWebSender;
import chabernac.p2p.io.WebToPeerSender;
import chabernac.protocol.routing.SocketPeer.StreamSplitterSupport;

public class PeerSender extends AbstractPeerSender{
  private static Logger LOGGER = Logger.getLogger(PeerSender.class);
  
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
      if(JVMPeerSender.getInstance().containsPeerProtocol(aMessage.getPeer().getPeerId())){
        return JVMPeerSender.getInstance().send(aMessage.getPeer().getPeerId(), aMessage.getMessage());
      }
      
      AbstractPeer theFrom = myRoutingTable.getEntryForLocalPeer().getPeer();
      AbstractPeer theTo = aMessage.getPeer();
      
//      if(theFrom.getPeerId() == aTo.getPeerId()) throw new IOException("You are sending a message to you self");

      if(theTo instanceof WebPeer){
        return myPeerToWebSender.sendMessageTo( theFrom, (WebPeer)theTo, aMessage.getMessage(), aTimeoutInSeconds );
      } else if(theFrom instanceof WebPeer){
        return myWebToPeerSender.sendMessageTo( (WebPeer)theFrom, theTo, aMessage.getMessage(), aTimeoutInSeconds ); 
      } else if(theTo instanceof SocketPeer){
        SocketPeer theSocketPeer = (SocketPeer)theTo;
        checkPortRange( theSocketPeer );
        if(myPeerToPeerSplitSender == null) theSocketPeer.setStreamSplittingSupported( false );
        
        if(theSocketPeer.isStreamSplittingSupported() == StreamSplitterSupport.UNKNOWN){
          try{
            theSocketPeer.setPeerId( myPeerToPeerSplitSender.getRemoteId( theSocketPeer ) );
            theSocketPeer.setStreamSplittingSupported( true );
          }catch(IOException e){
            theSocketPeer.setStreamSplittingSupported( false );
          }
        }
        
        //only if both ends support stream splitting then use it
        if(myPeerToPeerSplitSender != null &&  theSocketPeer.isStreamSplittingSupported() == StreamSplitterSupport.TRUE) {
          return myPeerToPeerSplitSender.sendMessageTo(aMessage, (SocketPeer)theTo, aMessage.getMessage(), aTimeoutInSeconds );
        }else { 
          return myPeerToPeerSender.sendMessageTo(aMessage, (SocketPeer)theTo, aMessage.getMessage(), aTimeoutInSeconds );
        }
      }
    }catch(Exception e){
      try {
        throw new IOException("Could not send message '" + aMessage.getMessage() + "' from '" + myRoutingTable.getEntryForLocalPeer().getPeer() + "' to '" +  aMessage.getPeer() + "'", e);
      } catch ( UnknownPeerException e1 ) {
      }
    }
    throw new IOException("Unsuported peer combination");
  }
  
  private void checkPortRange(SocketPeer aSocketPeer) throws IOException{
    if(aSocketPeer.getPort() < RoutingProtocol.START_PORT || aSocketPeer.getPort() > RoutingProtocol.END_PORT){
      throw new IOException("Port '" + aSocketPeer.getPort() + "' is not withing the current port range '" + RoutingProtocol.START_PORT + "' --> '" + RoutingProtocol.END_PORT + "'");
    }
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

  @Override
  public boolean isRemoteIdRetrievalAvailable( AbstractPeer aPeer ) {
    if(aPeer instanceof SocketPeer){
      SocketPeer theSocketPeer = (SocketPeer)aPeer;
      //only if both ends support stream splitting then use it
      if(myPeerToPeerSplitSender != null &&  theSocketPeer.isStreamSplittingSupported() == StreamSplitterSupport.TRUE){
        return true;
      }
    }
    return false;
  
  }

  @Override
  public String getRemoteId( AbstractPeer aPeer ) throws IOException {
    if(!isRemoteIdRetrievalAvailable( aPeer )) return null;
    return myPeerToPeerSplitSender.getRemoteId( (SocketPeer )aPeer );
  }
}
