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
  private static int LOGCOUNTER = 0;

  public String sendMessageTo(AbstractPeer aSendingPeer, WebPeer aWebPeer, String aMessage, int aTimeoutInSeconds) throws IOException{
    long t1 = System.currentTimeMillis();
    LOGGER.debug("Entering peer to web sender for message '" + aMessage + "' logcounter: " + LOGCOUNTER++);
    URLConnectionHelper theConnectionHelper = new URLConnectionHelper( aWebPeer.getURL(), ProtocolWebServer.CONTEXT_PROTOCOL, true );
    theConnectionHelper.scheduleClose( 30, TimeUnit.SECONDS );
    try{
      theConnectionHelper.connectInputOutput();
      long t2 = System.currentTimeMillis();
      LOGGER.debug("Connecting to '" + aWebPeer.getURL()  + "' took " + (t2 - t1) + " ms");
      theConnectionHelper.write( "session", UUID.randomUUID().toString() );
      theConnectionHelper.write( "peerid", aSendingPeer.getPeerId() );
      theConnectionHelper.write( "input", URLEncoder.encode(aMessage, "UTF-8") );
      long t3 = System.currentTimeMillis();
      LOGGER.debug("Writing to outputstream of '" + aWebPeer.getURL() + "' took " + (t3-t2) +  " ms");
//      theConnectionHelper.endLine();
//      theConnectionHelper.flush();
      theConnectionHelper.endInput();
      String theLine = theConnectionHelper.readLine();
      long t4 = System.currentTimeMillis();
      LOGGER.debug("Reading from '" + aWebPeer.getURL()  + "' took " + (t4-t3) +  " ms");
      
      return theLine;
    }catch(IOException e){
      LOGGER.error("Could not send message to web peer at endpoint: '" + aWebPeer.getEndPointRepresentation() + "'", e);
      //LOGGER.error("Could not send message to web peer at endpoint: '" + aWebPeer.getEndPointRepresentation() + "'");
      throw e;
    } finally {
      theConnectionHelper.close();
    }
  }
}
