package chabernac.p2p.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import chabernac.protocol.routing.AbstractPeer;
import chabernac.protocol.routing.WebPeer;

public class PeerToWebSender {
  private static Logger LOGGER = Logger.getLogger(PeerToWebSender.class);

  public String sendMessageTo(AbstractPeer aSendingPeer, WebPeer aWebPeer, String aMessage, int aTimeout) throws IOException{
    URL theCometURL = new URL(aWebPeer.getURL(), "p2p/protocol");
    URLConnection theConnection = null;
    BufferedReader theReader = null;
    OutputStreamWriter theWriter= null;
    ScheduledExecutorService theService = Executors.newScheduledThreadPool( 1 );
    try{
      theConnection = theCometURL.openConnection();
      theConnection.setDoOutput(true);
      if(aTimeout > 0) theService.schedule( new StreamCloser(theConnection.getOutputStream()), aTimeout, TimeUnit.SECONDS );
      theWriter = new OutputStreamWriter(theConnection.getOutputStream());
      theWriter.write("session=");
      theWriter.write(UUID.randomUUID().toString());
      theWriter.write("&peerid=");
      theWriter.write( aSendingPeer.getPeerId() );
      theWriter.write("&input=");
      theWriter.write(URLEncoder.encode(aMessage, "UTF-8"));
      theWriter.flush();


      theReader = new BufferedReader(new InputStreamReader(theConnection.getInputStream()));
      String theResponse = theReader.readLine();
      return theResponse;
    }catch(IOException e){
      LOGGER.error("Could not send message to web peer at endpoint: '" + aWebPeer.getEndPointRepresentation() + "'", e);
      throw e;
    } finally {
      theService.shutdownNow();
      if(theReader != null){
        try{
          theReader.close();
        }catch(IOException e){}
      }
      if(theWriter != null){
        try{
          theWriter.close();
        }catch(IOException e){}
      }
    }
  }
  
  private class StreamCloser implements Runnable{
    private final OutputStream myOutputStream;

    public StreamCloser ( OutputStream anOutputStream) {
      super();
      myOutputStream = anOutputStream;
    }

    public void run(){
      try {
        myOutputStream.close();
      } catch ( IOException e ) {
      }
    }
  }
}
