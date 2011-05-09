package chabernac.p2p.io;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import chabernac.comet.CometEvent;
import chabernac.comet.EndPoint;
import chabernac.protocol.routing.AbstractPeer;
import chabernac.protocol.routing.WebPeer;

public class WebToPeerSender {


  public String sendMessageTo(WebPeer aSendingPeer, AbstractPeer aPeer, String aMessage, int aTimeoutInSeconds) throws IOException{
    if(aSendingPeer.getEndPointContainer() == null) throw new IOException("No endpoints available in webpeer '" + aSendingPeer.getPeerId() + "'");
    if(aSendingPeer.getEndPointContainer().getNrOfEndPoints( aPeer.getPeerId() ) == 0) throw new IOException("No end point available for peer '" + aPeer.getPeerId() + "' in webpeer '" + aSendingPeer.getPeerId() + "'");

    try{
      EndPoint theEndPoint = aSendingPeer.getEndPointContainer().getEndPointFor( aPeer.getPeerId(), aTimeoutInSeconds, TimeUnit.SECONDS );
      UUID theUID = UUID.randomUUID();
      CometEvent theCometEvent = new CometEvent(theUID.toString(), aMessage);
      theEndPoint.setEvent( theCometEvent );
      return theCometEvent.getOutput(5000).replaceAll("\\{plus\\}", "+");
    }catch(Exception e){
      throw new IOException("Could not send message to peer '" + aPeer.getPeerId() + "' from webpeer '" + aSendingPeer.getPeerId() + "'");
    }

  }
}
