package chabernac.p2p.io;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import chabernac.io.AbstractURLConnectionHelper;
import chabernac.io.HttpCommunicationInterface;
import chabernac.io.URLConnectionHelper;
import chabernac.protocol.ProtocolWebServer;
import chabernac.protocol.routing.AbstractPeer;
import chabernac.protocol.routing.PeerSenderReply;
import chabernac.protocol.routing.WebPeer;

public class PeerToWebSender {
  private static Logger LOGGER = Logger.getLogger(PeerToWebSender.class);
  private HttpCommunicationInterface myCommunicationInterface = new HttpCommunicationInterface();
//  private static int LOGCOUNTER = 0;

  public PeerSenderReply sendMessageTo(AbstractPeer aSendingPeer, WebPeer aWebPeer, String aMessage, int aTimeoutInSeconds) throws IOException{
    synchronized (aWebPeer.getPeerId()) {

//      long t1 = System.currentTimeMillis();
//      LOGGER.debug("Entering peer to web sender for message '" + aMessage + "' logcounter: " + LOGCOUNTER++);
      //    AbstractURLConnectionHelper theConnectionHelper = new ApacheURLConnectionHelper( new URL(aWebPeer.getURL(), ProtocolWebServer.CONTEXT_PROTOCOL), true );
      AbstractURLConnectionHelper theConnectionHelper = new URLConnectionHelper( new URL(aWebPeer.getURL(), ProtocolWebServer.CONTEXT_PROTOCOL), true );
      theConnectionHelper.scheduleClose( 30, TimeUnit.SECONDS );
      try{
        theConnectionHelper.connectInputOutput();
//        long t2 = System.currentTimeMillis();
//        LOGGER.debug("Connecting to '" + aWebPeer.getURL()  + "' took " + (t2 - t1) + " ms");
        theConnectionHelper.write( "session", UUID.randomUUID().toString() );
        theConnectionHelper.write( "peerid", aSendingPeer.getPeerId() );
        //      theConnectionHelper.write( "input", aMessage );
        theConnectionHelper.write( "input", URLEncoder.encode(aMessage, "UTF-8") );
//        long t3 = System.currentTimeMillis();
//        LOGGER.debug("Writing to outputstream of '" + aWebPeer.getURL() + "' took " + (t3-t2) +  " ms");
        //      theConnectionHelper.endLine();
        //      theConnectionHelper.flush();
        theConnectionHelper.endInput();
        String theLine = theConnectionHelper.readLine();
//        long t4 = System.currentTimeMillis();
//        LOGGER.debug("Reading from '" + aWebPeer.getURL()  + "' took " + (t4-t3) +  " ms");

        return new PeerSenderReply( theLine, myCommunicationInterface);
      }catch(IOException e){
        LOGGER.error("Could not send message to web peer at endpoint: '" + aWebPeer.getEndPointRepresentation() + "'", e);
        //LOGGER.error("Could not send message to web peer at endpoint: '" + aWebPeer.getEndPointRepresentation() + "'");
        throw e;
      } finally {
        theConnectionHelper.close();
      }
    }
  }
  /*
  public String sendMessageTo(AbstractPeer aSendingPeer, WebPeer aWebPeer, String aMessage, int aTimeoutInSeconds) throws IOException{
    long t1 = System.currentTimeMillis();
    HttpParams theHttpParams = new BasicHttpParams();
    theHttpParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
    HttpClient httpclient = new DefaultHttpClient(theHttpParams);
    URL theURL = NetTools.resolveURL(new URL(aWebPeer.getURL(),  ProtocolWebServer.CONTEXT_PROTOCOL));
    try{
      List<NameValuePair> formparams = new ArrayList<NameValuePair>();
      formparams.add(new BasicNameValuePair("session", UUID.randomUUID().toString()));
      formparams.add(new BasicNameValuePair("peerid", aSendingPeer.getPeerId() ));
      formparams.add(new BasicNameValuePair("input", aMessage));
      UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
      HttpPost thePost = new HttpPost(theURL.toURI());
      thePost.setEntity(entity);
      HttpResponse theResponse = httpclient.execute(thePost);
      long t2 = System.currentTimeMillis();
      LOGGER.debug("Connecting to '" + aWebPeer.getURL()  + "' took " + (t2 - t1) + " ms");

      HttpEntity theEntity = theResponse.getEntity();
      if(theEntity != null){
        BufferedReader theReader = new BufferedReader(new InputStreamReader(theEntity.getContent()));
        String theLine = theReader.readLine();
        long t3 = System.currentTimeMillis();
        LOGGER.debug("Total time for reading from '" + aWebPeer.getURL()  + "' took " + (t3 - t1) + " ms");
        return theLine;
      }
      throw new IOException("Empty response received");
    }catch(Exception e){
      throw new IOException("Could not send message", e);
    }
  }
   */
}
