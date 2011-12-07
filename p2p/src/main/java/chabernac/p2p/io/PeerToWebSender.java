package chabernac.p2p.io;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import chabernac.io.URLConnectionHelper;
import chabernac.protocol.ProtocolWebServer;
import chabernac.protocol.routing.AbstractPeer;
import chabernac.protocol.routing.WebPeer;

public class PeerToWebSender {
  private static Logger LOGGER = Logger.getLogger(PeerToWebSender.class);

  public String sendMessageTo(AbstractPeer aSendingPeer, WebPeer aWebPeer, String aMessage, int aTimeoutInSeconds) throws IOException{
    
    URLConnectionHelper theConnectionHelper = new URLConnectionHelper( aWebPeer.getURL(), ProtocolWebServer.CONTEXT_PROTOCOL );
    theConnectionHelper.scheduleClose( aTimeoutInSeconds, TimeUnit.SECONDS );
    try{
      theConnectionHelper.connectInputOutput();
      theConnectionHelper.write( "session", UUID.randomUUID().toString() );
      theConnectionHelper.write( "peerid", aSendingPeer.getPeerId() );
      theConnectionHelper.write( "input", URLEncoder.encode(aMessage, "UTF-8") );
      theConnectionHelper.flush();
      return theConnectionHelper.readLine();
    }catch(IOException e){
      LOGGER.error("Could not send message to web peer at endpoint: '" + aWebPeer.getEndPointRepresentation() + "'", e);
      //LOGGER.error("Could not send message to web peer at endpoint: '" + aWebPeer.getEndPointRepresentation() + "'");
      throw e;
    } finally {
      theConnectionHelper.close();
    }
  }
}
