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
    URLConnectionHelper theConnectionHelper = new URLConnectionHelper( myURL, "p2p/comet" );
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

//  public CometEvent waitForEvent(String aLocalPeerId) throws IOException{
//    URL theCometURL = new URL(myURL, "p2p/comet");
//    final URLConnection theConnection = theCometURL.openConnection();
//    theConnection.setDoOutput(true);
//    OutputStreamWriter theWriter = null;
//    BufferedReader theReader = null;
//
//    try{
//      theWriter = new OutputStreamWriter(theConnection.getOutputStream());
//      theWriter.write("id=" + aLocalPeerId);
//      theWriter.flush();
//      final ScheduledExecutorService theService = Executors.newScheduledThreadPool(1);
//      theService.schedule(new Runnable(){
//        public void run(){
//          ((HttpURLConnection)theConnection).disconnect();
//          theService.shutdownNow();
//        }
//      }, TIMEOUT_IN_MINUTES, TimeUnit.MINUTES);
//      theReader = new BufferedReader(new InputStreamReader(theConnection.getInputStream()));
//      String theEvent = theReader.readLine();
//      //    LOGGER.debug("Received comet event line '" + theEvent + "'");
//      CometEvent theCometEvent = getCometStringConverter().getObject( theEvent );
//      getExecutorService().execute( new CometEventResponseSender(theCometEvent) );
//      return theCometEvent;
//    } finally {
//      if(theReader != null){
//        try{
//          theReader.close();
//        }catch(IOException e){
//          LOGGER.error("Could not close input stream", e);
//        }
//      }
//      if(theWriter != null){
//        try{
//          theWriter.close();
//        } catch(IOException e){
//          LOGGER.error("Could not close writer", e);
//        }
//      }
//      if(theConnection != null){
//        ((HttpURLConnection)theConnection).disconnect();
//      }
//    }
//
//  }

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
    URLConnectionHelper theConnectionHelper = new URLConnectionHelper( myURL, "p2p/comet" );
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
//    
//    OutputStreamWriter theWriter = null;
//    BufferedReader theReader = null;
//    URLConnection theConnection = null;
//    try{
//      URL theCometURL = new URL(myURL, "p2p/comet");
//      theConnection = theCometURL.openConnection();
//      theConnection.setDoOutput(true);
//      theWriter = new OutputStreamWriter(theConnection.getOutputStream());
//      theWriter.write("id=" + getPeerId() + "&eventid=" + anEvent.getId() + "&eventoutput=" + anEvent.getOutput( 0 ).replaceAll("\\+", "{plus}"));
//      theWriter.flush();
//      theReader = new BufferedReader(new InputStreamReader(theConnection.getInputStream()));
//      String theResult = theReader.readLine();
//      return theResult.equalsIgnoreCase( "OK" );
//    }catch(CometException e){
//      throw new IOException("Could not send response for comet event", e);
//    } finally {
//      if(theWriter != null){
//        try{
//          theWriter.close();
//        }catch(IOException e){
//          LOGGER.error("Could not close outputstream", e);
//        }
//      }
//      if(theReader != null){
//        try{
//          theReader.close();
//        }catch(IOException e){
//          LOGGER.error("Could not close input stream", e);
//        }
//      }
//      if(theConnection != null){
//        ((HttpURLConnection)theConnection).disconnect();
//      }
//    }
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
