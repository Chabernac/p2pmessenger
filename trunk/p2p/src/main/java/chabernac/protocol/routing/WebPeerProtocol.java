package chabernac.protocol.routing;

import java.net.SocketException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import chabernac.comet.CometEvent;
import chabernac.protocol.IProtocol;
import chabernac.protocol.Protocol;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;

public class WebPeerProtocol extends Protocol{
  private static Logger LOGGER = Logger.getLogger(WebPeerProtocol.class);
  public static final String ID = "WPP";
  private static final int MAX_ERRORS = 10;
  private static final int MAX_THREADS_PER_PEER = 1;

  public static enum Input{EVENT};
  public static enum Response{UNKNOWN_COMMAND};

  private Map<WebPeer, WebPeerEventListener> myListeners = Collections.synchronizedMap(new HashMap<WebPeer, WebPeerEventListener>());
  private ExecutorService myWebPeerListenerService = Executors.newCachedThreadPool();
  
  private AtomicInteger myListenerCounter = new AtomicInteger(0);

  public WebPeerProtocol() throws ProtocolException {
    super(ID);
  }

  private void addRemoveWebPeerListeners() throws ProtocolException {
    for(RoutingTableEntry theEntry : getRoutingTable()){
      if(theEntry.getPeer() instanceof WebPeer){
        WebPeer theWebPeer = (WebPeer)theEntry.getPeer();
        checkWebPeerListenerForWebPeer(theWebPeer, theEntry.getHopDistance());
      }
    }
  }
  
  private synchronized void checkWebPeerListenerForWebPeer(WebPeer aWebPeer, int aHopDistance){
    if(aHopDistance == 1  && !myListeners.containsKey(aWebPeer)){
      WebPeerEventListener theListener = new WebPeerEventListener(aWebPeer);
      myListeners.put(aWebPeer, theListener);
      myWebPeerListenerService.execute(theListener);
    } else if(aHopDistance > 1 && myListeners.containsKey(aWebPeer)){
      WebPeerEventListener theWebPeerEventListener = myListeners.get(aWebPeer);
      theWebPeerEventListener.stop();
    }
  }

  public void setMasterProtocol(IProtocol aProtocol){
    super.setMasterProtocol(aProtocol);
    try {
      addListener();
      addRemoveWebPeerListeners();
    } catch (ProtocolException e) {
      LOGGER.error("Unable to add listener", e);
    }
  }

  private ProtocolContainer getProtocolContainer(){
    return (ProtocolContainer)getMasterProtocol();
  }

  public RoutingTable getRoutingTable() throws ProtocolException{
    return ((RoutingProtocol)findProtocolContainer().getProtocol( RoutingProtocol.ID )).getRoutingTable();
  }

  private void addListener() throws ProtocolException{
    getRoutingTable().addRoutingTableListener(new RoutingTableListener());
  }

  @Override
  public String getDescription() {
    return "Web peer protocol";
  }

  @Override
  public String handleCommand(String aSessionId, String anInput) {
    if(anInput.startsWith(Input.EVENT.name())){
      String theProtocolPart = anInput.substring(Input.EVENT.name().length() + 1);
      return getProtocolContainer().handleCommand(aSessionId, theProtocolPart);
    }
    return Response.UNKNOWN_COMMAND.name();
  }

  @Override
  public void stop() {
    for(WebPeerEventListener theListener : myListeners.values()){
      theListener.stop();
    }
    myWebPeerListenerService.shutdown();
  }

  private class RoutingTableListener implements IRoutingTableListener {

    @Override
    public void routingTableEntryChanged(RoutingTableEntry anEntry) {
      try {
        addRemoveWebPeerListeners();
      } catch (ProtocolException e) {
        LOGGER.error("An error occured while setting up web peer listeners", e);
      }
    }

    @Override
    public void routingTableEntryRemoved(RoutingTableEntry anEntry) {
      try {
        addRemoveWebPeerListeners();
      } catch (ProtocolException e) {
        LOGGER.error("An error occured while setting up web peer listeners", e);
      }
    }
  }

  private class WebPeerEventListener implements Runnable{
    private final WebPeer myWebPeer;
    private boolean stop = false;
//    private AtomicInteger myConcurrentThreads = new AtomicInteger(0);

    public WebPeerEventListener(WebPeer anWebPeer) {
      super();
      myWebPeer = anWebPeer;
    }

    public void stop() {
      stop = true;
    }

    @Override
    public void run() {
      myListenerCounter.incrementAndGet();
      LOGGER.debug("Nr of web peer listeners: '"  + myListenerCounter.get()  + "'");
      
      int theErrors = 0;
      
      try{
        while(!stop && theErrors < MAX_ERRORS ){
          try{
            List<CometEvent> theEvents = myWebPeer.waitForEvents(getRoutingTable().getLocalPeerId());
            
            for(CometEvent theEvent : theEvents){
              String theResult = handleCommand(UUID.randomUUID().toString(), Input.EVENT.name() + " " + theEvent.getInput());
              theEvent.setOutput( theResult );
            }
          }catch(SocketException e){
            LOGGER.debug("Socket for peer '" + myWebPeer.getPeerId() + "' timed out");
          }catch(Exception e){
            theErrors++;
            LOGGER.error("An error occured while waiting for event from webpeer '" + myWebPeer.getPeerId() + "' error counter='" + theErrors + "'", e);
            try {
              Thread.sleep( 500 );
            } catch ( InterruptedException e1 ) {
            }
          }
        }
      } finally {
        myListeners.remove(myWebPeer);
      }
    }
  }
}
