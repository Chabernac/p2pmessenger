package chabernac.protocol.routing;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import chabernac.comet.CometEvent;
import chabernac.comet.CometException;
import chabernac.comet.EndPointContainer;
import chabernac.io.Base64ObjectStringConverter;
import chabernac.io.URLConnectionHelper;
import chabernac.io.iObjectStringConverter;
import chabernac.protocol.ProtocolWebServer;

public class WebPeer extends AbstractPeer {
  private static final long serialVersionUID = -6488979114630311123L;
  public static final int TIMEOUT_IN_MINUTES = 15;
  private static Logger LOGGER = Logger.getLogger(WebPeer.class);

  private final URL myURL;
  private transient iObjectStringConverter< CometEvent > myObjectStringConverter = new Base64ObjectStringConverter< CometEvent >();

  private transient ExecutorService myService = Executors.newCachedThreadPool();
  private transient EndPointContainer myEndPointContainer = null;

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

  public EndPointContainer getEndPointContainer() {
    return myEndPointContainer;
  }

  public void setEndPointContainer( EndPointContainer aEndPointContainer ) {
    myEndPointContainer = aEndPointContainer;
  }
  
  public CometEvent waitForEvent(String aLocalPeerId) throws IOException{
    URLConnectionHelper theConnectionHelper = new URLConnectionHelper( myURL, ProtocolWebServer.CONTEXT_COMET );
    try{
      theConnectionHelper.connectInputOutput();
      theConnectionHelper.scheduleClose(TIMEOUT_IN_MINUTES, TimeUnit.MINUTES);
      theConnectionHelper.write( "id", aLocalPeerId );
      theConnectionHelper.flush();
      String theEvent = theConnectionHelper.readLine();
      if(theEvent == null) throw new IOException("Empty response received from webnode at '" + myURL + "'");
      CometEvent theCometEvent = getCometStringConverter().getObject( theEvent );
      getExecutorService().execute( new CometEventResponseSender(theCometEvent) );
      return theCometEvent;
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
    URLConnectionHelper theConnectionHelper = new URLConnectionHelper( myURL, ProtocolWebServer.CONTEXT_COMET );
    try{
      theConnectionHelper.connectInputOutput();
      theConnectionHelper.scheduleClose(TIMEOUT_IN_MINUTES, TimeUnit.MINUTES);
      theConnectionHelper.write( "id", getPeerId() );
      theConnectionHelper.write( "eventid", anEvent.getId() );
      theConnectionHelper.write( "eventoutput", anEvent.getOutput( 0 ).replaceAll("\\+", "{plus}") );
      theConnectionHelper.flush();
      
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
