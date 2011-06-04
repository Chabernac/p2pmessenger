package chabernac.protocol.routing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import chabernac.comet.CometEvent;
import chabernac.comet.CometException;
import chabernac.comet.EndPointContainer;
import chabernac.io.Base64ObjectStringConverter;
import chabernac.io.iObjectStringConverter;

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
    URL theCometURL = new URL(myURL, "p2p/comet");
    final URLConnection theConnection = theCometURL.openConnection();
    theConnection.setDoOutput(true);
    OutputStreamWriter theWriter = new OutputStreamWriter(theConnection.getOutputStream());
    theWriter.write("id=" + aLocalPeerId);
    theWriter.flush();
    Executors.newScheduledThreadPool(1).schedule(new Runnable(){
      public void run(){
          ((HttpURLConnection)theConnection).disconnect();
      }
    }, TIMEOUT_IN_MINUTES, TimeUnit.MINUTES);
    BufferedReader theReader = new BufferedReader(new InputStreamReader(theConnection.getInputStream()));
    String theEvent = theReader.readLine();
//    LOGGER.debug("Received comet event line '" + theEvent + "'");
    CometEvent theCometEvent = getCometStringConverter().getObject( theEvent );
    getExecutorService().execute( new CometEventResponseSender(theCometEvent) );
    return theCometEvent;
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
    try{
      URL theCometURL = new URL(myURL, "p2p/comet");
      URLConnection theConnection = theCometURL.openConnection();
      theConnection.setDoOutput(true);
      OutputStreamWriter theWriter = new OutputStreamWriter(theConnection.getOutputStream());
      theWriter.write("id=" + getPeerId() + "&eventid=" + anEvent.getId() + "&eventoutput=" + anEvent.getOutput( 0 ).replaceAll("\\+", "{plus}"));
      theWriter.flush();
      BufferedReader theReader = new BufferedReader(new InputStreamReader(theConnection.getInputStream()));
      String theResult = theReader.readLine();
      return theResult.equalsIgnoreCase( "OK" );
    }catch(CometException e){
      throw new IOException("Could not send response for comet event", e);
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
}
