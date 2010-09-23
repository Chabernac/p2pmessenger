package chabernac.protocol.routing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import chabernac.comet.CometEvent;
import chabernac.comet.CometException;
import chabernac.io.Base64ObjectStringConverter;
import chabernac.io.iObjectStringConverter;

public class WebPeer extends AbstractPeer {
  private static Logger LOGGER = Logger.getLogger(WebPeer.class);

  private final URL myURL;
  private iObjectStringConverter< CometEvent > myObjectStringConverter = new Base64ObjectStringConverter< CometEvent >();

  private ExecutorService myService = Executors.newCachedThreadPool();

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

  public CometEvent waitForEvent() throws IOException{
    URL theCometURL = new URL(myURL, "p2p/comet");
    URLConnection theConnection = theCometURL.openConnection();
    theConnection.setDoOutput(true);
    OutputStreamWriter theWriter = new OutputStreamWriter(theConnection.getOutputStream());
    theWriter.write("id=" + getPeerId());
    theWriter.flush();
    BufferedReader theReader = new BufferedReader(new InputStreamReader(theConnection.getInputStream()));
    String theEvent = theReader.readLine();
    CometEvent theCometEvent = myObjectStringConverter.getObject( theEvent );
    myService.execute( new CometEventResponseSender(theCometEvent) );
    return theCometEvent;
  }

  private boolean sendResponseForCometEvent( CometEvent anEvent ) throws IOException
  {
    try{
      URL theCometURL = new URL(myURL, "p2p/comet");
      URLConnection theConnection = theCometURL.openConnection();
      theConnection.setDoOutput(true);
      OutputStreamWriter theWriter = new OutputStreamWriter(theConnection.getOutputStream());
      theWriter.write("id=" + getPeerId() + "&eventid=" + anEvent.getId() + "&eventoutput=" + anEvent.getOutput( 0 ));
      theWriter.flush();
      BufferedReader theReader = new BufferedReader(new InputStreamReader(theConnection.getInputStream()));
      String theResult = theReader.readLine();
      return theResult.equalsIgnoreCase( "OK" );
    }catch(CometException e){
      throw new IOException("Could not send response for comet event", e);
    }
  }


  @Override
  public String send(String aMessage) throws IOException{
    return send(aMessage, 5);
  }

  public String send(String aMessage, int aTimeoutInSeconds) throws IOException {
    if(PeerSenderHolder.getPeerSender() == null) throw new IOException("Could not send message to peer '" + getPeerId() + " because no message sender was defined");

    return PeerSenderHolder.getPeerSender().send(aMessage, this, aTimeoutInSeconds);
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
