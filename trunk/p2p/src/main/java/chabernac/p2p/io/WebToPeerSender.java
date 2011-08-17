package chabernac.p2p.io;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import chabernac.comet.CometEvent;
import chabernac.comet.EndPoint;
import chabernac.protocol.routing.AbstractPeer;
import chabernac.protocol.routing.WebPeer;

public class WebToPeerSender {
  private Map<String, AtomicInteger> myPendingMessagesForPeer = Collections.synchronizedMap( new HashMap<String, AtomicInteger>());


  public String sendMessageTo(WebPeer aSendingPeer, AbstractPeer aPeer, String aMessage, int aTimeoutInSeconds) throws IOException{
    if(aSendingPeer.getEndPointContainer() == null) throw new IOException("No endpoints available in webpeer '" + aSendingPeer.getPeerId() + "'");

    
    try{
      int thePendingMessages = incrementCounter(aPeer.getPeerId());
      
      EndPoint theEndPoint = aSendingPeer.getEndPointContainer().getEndPointFor( aPeer.getPeerId(), aTimeoutInSeconds, TimeUnit.SECONDS );
      if(theEndPoint == null) throw new IOException("No end point available for peer '" + aPeer.getPeerId() + "' in webpeer '" + aSendingPeer.getPeerId() + "'");
      UUID theUID = UUID.randomUUID();
      CometEvent theCometEvent = new CometEvent(theUID.toString(), aMessage);
      theCometEvent.setPendingEvents( thePendingMessages - 1);
      theEndPoint.setEvent( theCometEvent );
      return theCometEvent.getOutput(5000).replaceAll("\\{plus\\}", "+");
    }catch(Exception e){
      throw new IOException("Could not send message to peer '" + aPeer.getPeerId() + "' from webpeer '" + aSendingPeer.getPeerId() + "'", e);
    }finally{
      decrementCounter( aPeer.getPeerId() );
    }

  }


  private int incrementCounter( String aPeerId ) {
    synchronized(aPeerId){
      if(!myPendingMessagesForPeer.containsKey( aPeerId )){
        myPendingMessagesForPeer.put( aPeerId, new AtomicInteger(0) );
      }
      
      return myPendingMessagesForPeer.get(aPeerId).incrementAndGet();
    }
  }
  
  private int decrementCounter( String aPeerId ){
    synchronized(aPeerId){
      int theCounter = myPendingMessagesForPeer.get( aPeerId ).decrementAndGet();
      if(theCounter <= 0){
        myPendingMessagesForPeer.remove( aPeerId );
      }
      return theCounter;
    }
  }
}
