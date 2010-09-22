package chabernac.protocol.routing;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import chabernac.comet.CometEvent;
import chabernac.io.Base64ObjectStringConverter;
import chabernac.protocol.Protocol;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;

public class WebPeerListener extends Protocol{
  private static Logger LOGGER = Logger.getLogger(WebPeerEventListener.class);
  public static final String ID = "WPL";
  
  public static enum Input{EVENT};
  public static enum Response{UNKNOWN_COMMAND};
  
  private Map<WebPeer, WebPeerEventListener> myListeners = Collections.synchronizedMap(new HashMap<WebPeer, WebPeerEventListener>());
  private ExecutorService myWebPeerListenerService = Executors.newCachedThreadPool();
  private Base64ObjectStringConverter<Event> myEventConvert = new Base64ObjectStringConverter<Event>();

  public WebPeerListener() throws ProtocolException {
    super(ID);
    addRemoveWebPeerListeners();
    addListener();
  }

  private void addRemoveWebPeerListeners() throws ProtocolException {
    for(RoutingTableEntry theEntry : getRoutingTable()){
      if(theEntry.getPeer() instanceof WebPeer){
        WebPeer theWebPeer = (WebPeer)theEntry.getPeer();
        if(theEntry.getHopDistance() == 1  && !myListeners.containsKey(theWebPeer)){
          myWebPeerListenerService.execute(new WebPeerEventListener(theWebPeer));
        } else if(theEntry.getHopDistance() > 1 && myListeners.containsKey(theWebPeer)){
          WebPeerEventListener theWebPeerEventListener = myListeners.get(theWebPeer);
          theWebPeerEventListener.stop();
        }
      }
    }

  }
  
  private ProtocolContainer getProtocolContainer(){
    return (ProtocolContainer)getMasterProtocol();
  }

  private RoutingTable getRoutingTable() throws ProtocolException{
    ProtocolContainer theProtocolContainer = getProtocolContainer();
    RoutingProtocol theRoutingProtocol = (RoutingProtocol)theProtocolContainer.getProtocol(RoutingProtocol.ID);
    return theRoutingProtocol.getRoutingTable();
  }

  private void addListener() throws ProtocolException{
    getRoutingTable().addRoutingTableListener(new RoutingTableListener());
  }
  
  @Override
  public String getDescription() {
    return "Web peer protocol";
  }

  @Override
  public String handleCommand(long aSessionId, String anInput) {
    if(anInput.startsWith(Input.EVENT.name())){
      return getProtocolContainer().handleCommand(aSessionId, anInput);
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

    public WebPeerEventListener(WebPeer anWebPeer) {
      super();
      myWebPeer = anWebPeer;
    }

    public void stop() {
      stop = true;
    }

    @Override
    public void run() {
      myListeners.put(myWebPeer, this);
      try{
        while(!stop){
          CometEvent theEvent = myWebPeer.waitForEvent();
          String theResult = handleCommand(-1, Input.EVENT.name() + " " + theEvent.getInput());
          theEvent.setOutput( theResult );
        }
      }catch(IOException e){
        LOGGER.error("An error occured while waiting for event from webpeer '" + myWebPeer.getPeerId() + "'", e);
      } finally {
        myListeners.remove(myWebPeer);
      }
    }
  }
}
