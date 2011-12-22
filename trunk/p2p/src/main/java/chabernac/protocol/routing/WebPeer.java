package chabernac.protocol.routing;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import chabernac.comet.CometEvent;
import chabernac.comet.CometException;
import chabernac.io.AbstractURLConnectionHelper;
import chabernac.io.ApacheURLConnectionHelper;
import chabernac.io.Base64ObjectStringConverter;
import chabernac.io.iObjectStringConverter;
import chabernac.newcomet.EndPointContainer2;
import chabernac.protocol.ProtocolWebServer;

public class WebPeer extends AbstractPeer {
  private static final long serialVersionUID = -6488979114630311123L;
  public static final int TIMEOUT_IN_MINUTES = 15;
  private static Logger LOGGER = Logger.getLogger(WebPeer.class);

  private final URL myURL;
  private transient iObjectStringConverter< CometEvent > myObjectStringConverter = new Base64ObjectStringConverter< CometEvent >();

  private transient ExecutorService myService = Executors.newCachedThreadPool();
  private transient EndPointContainer2 myEndPointContainer = null;

  public WebPeer(){
    this(null);
  }

  public WebPeer(URL anUrl) {
    this("", anUrl);
  }

  public WebPeer(String aPeerId, URL anUrl) {
    super(aPeerId);
    myURL = anUrl;
  }
  
  public WebPeer(URL anURL, WebPeer aWebPeer ){
   this(aWebPeer.getPeerId(), anURL);
   setChannel( aWebPeer.getChannel() );
   setTestPeer( aWebPeer.isTestPeer() );
   setTemporaryPeer( aWebPeer.isTemporaryPeer() );
   mySupportedProtocols.clear();
   mySupportedProtocols.addAll(aWebPeer.getSupportedProtocols());
  }

  @Override
  public String getEndPointRepresentation() {
    return myURL.toString();
  }

  @Override
  public boolean isSameEndPointAs(AbstractPeer aPeer) {
    if(!(aPeer instanceof WebPeer)) return false;
    WebPeer thePeer = (WebPeer)aPeer;

    return getURL().equals(thePeer.getURL());
  }

  public URL getURL() {
    return myURL;
  }

  @Override
  public boolean isValidEndPoint() {
    return myURL != null;
  }

  public EndPointContainer2 getEndPointContainer() {
    return myEndPointContainer;
  }

  public void setEndPointContainer( EndPointContainer2 aEndPointContainer ) {
    myEndPointContainer = aEndPointContainer;
  }
  
  public List<CometEvent> waitForEvents(String aLocalPeerId) throws IOException{
    AbstractURLConnectionHelper theConnectionHelper = new ApacheURLConnectionHelper( new URL(myURL, ProtocolWebServer.CONTEXT_COMET), true );
    try{
      LOGGER.debug("Waiting for event from webpeer '" + getPeerId() + "'");
      theConnectionHelper.connectInputOutput();
      theConnectionHelper.scheduleClose(TIMEOUT_IN_MINUTES, TimeUnit.MINUTES);
      theConnectionHelper.write( "id", aLocalPeerId );
      theConnectionHelper.endInput();
      List<CometEvent> theEvents= new ArrayList<CometEvent>();
      String theEvent = null;
      while((theEvent = theConnectionHelper.readLine()) != null){
        CometEvent theCometEvent = getCometStringConverter().getObject( theEvent );
        //the comet event can not yet be expired at this time, 
        theCometEvent.setExpired(false);
        getExecutorService().execute( new CometEventResponseSender(theCometEvent) );
        theEvents.add(theCometEvent);
      }
      LOGGER.debug("Got '" + theEvents.size() + "' events from webpeer");
      return theEvents;
    } finally {
      theConnectionHelper.close();
    }
  }

  private iObjectStringConverter<CometEvent> getCometStringConverter(){
    if(myObjectStringConverter == null) myObjectStringConverter = new Base64ObjectStringConverter<CometEvent>();
    return myObjectStringConverter;
  }

  private ExecutorService getExecutorService(){
    if(myService == null) myService = Executors.newSingleThreadExecutor();
    return myService;
  }

  private boolean sendResponseForCometEvent( CometEvent anEvent ) throws IOException
  {
    AbstractURLConnectionHelper theConnectionHelper = new ApacheURLConnectionHelper( new URL(myURL, ProtocolWebServer.CONTEXT_COMET), true );
    try{
      theConnectionHelper.connectInputOutput();
      theConnectionHelper.scheduleClose(TIMEOUT_IN_MINUTES, TimeUnit.MINUTES);
      theConnectionHelper.write( "id", getPeerId() );
      theConnectionHelper.write( "eventid", anEvent.getId() );
      theConnectionHelper.write( "eventoutput", anEvent.getOutput( 0 ).replaceAll("\\+", "{plus}") );
      theConnectionHelper.endInput();
      
      return theConnectionHelper.readLine().equalsIgnoreCase( "OK" );
    }catch(CometException e){
      throw new IOException("Could not send response for comet event", e);
    } finally {
      theConnectionHelper.close();
    }
  }

  public String toString(){
    StringBuilder theBuilder = new StringBuilder();
    theBuilder.append( getPeerId() );
    theBuilder.append("@");
    theBuilder.append(getChannel());
    theBuilder.append( " (" );
    theBuilder.append( myURL.toString() );
    theBuilder.append( ")" );
    return theBuilder.toString();
  }


  private class CometEventResponseSender implements Runnable{
    private final CometEvent myEvent;

    public CometEventResponseSender ( CometEvent anEvent ) {
      super();
      myEvent = anEvent;
    }

    @Override
    public void run() {
      try {
        myEvent.getOutput( 5000 );
        sendResponseForCometEvent(myEvent);
      } catch ( Exception e ) {
        LOGGER.error("No response for comet event", e);
      }
    }
  }


  @Override
  public boolean isContactable() {
    return true;
  }
}
