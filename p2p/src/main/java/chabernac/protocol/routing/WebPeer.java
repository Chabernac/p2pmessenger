package chabernac.protocol.routing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import com.sun.mail.handlers.message_rfc822;

public class WebPeer extends AbstractPeer {
  private final URL myURL;
  
  public WebPeer(URL anUrl) {
    super();
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

  @Override
  public String send(String aMessage) throws IOException {
    URLConnection theConnection = myURL.openConnection();
    theConnection.setDoOutput(true);
    OutputStreamWriter theWriter = new OutputStreamWriter(theConnection.getOutputStream());
    theWriter.write("id=" + getPeerId() + "data=" + aMessage);
    theWriter.flush();
    BufferedReader theReader = new BufferedReader(new InputStreamReader(theConnection.getInputStream()));
    return theReader.readLine();
  }
  
  public String waitForServerEvent() throws IOException{
    return send(null);
  }

}
