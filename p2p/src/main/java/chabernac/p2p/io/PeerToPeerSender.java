package chabernac.p2p.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import chabernac.io.SimpleNetworkInterface;
import chabernac.io.SocketProxy;
import chabernac.io.StreamSplitter;
import chabernac.io.StreamSplitterPool;
import chabernac.p2p.settings.P2PSettings;
import chabernac.protocol.routing.PeerMessage;
import chabernac.protocol.routing.PeerMessage.State;
import chabernac.protocol.routing.PeerSenderReply;
import chabernac.protocol.routing.SocketPeer;
import chabernac.protocol.routing.SocketPeer.StreamSplitterSupport;
import chabernac.utils.NamedRunnable;
import chabernac.utils.NetTools;

public class PeerToPeerSender {
  private static final Logger LOGGER = Logger.getLogger(PeerToPeerSender.class);

  public PeerSenderReply sendMessageTo(PeerMessage aPeerMessage, SocketPeer aPeer, String aMessage, int aTimeoutInSeconds) throws IOException {
    if(aPeer.isStreamSplittingSupported() == StreamSplitterSupport.TRUE){
      //just to be compatible with the stream splitters
      aMessage = StreamSplitter.IN + aMessage;
    }
    
    RetryDecider theRetryDecider = new RetryDecider(3);

    while(theRetryDecider.retry()){
      theRetryDecider.clear();
      SocketProxy theSocket = aPeer.createSocket( aPeer.getPort() );

      if(theSocket == null) {
        throw new IOException("Could not open socket to peer: " + aPeer.getPeerId() + " " + aPeer.getHosts() + ":" + aPeer.getPort());
      }

      theRetryDecider.socketCreated();

      ScheduledExecutorService theService = Executors.newScheduledThreadPool( 1 );

      BufferedReader theReader = null;
      PrintWriter theWriter = null;
      try{
        if(aTimeoutInSeconds > 0) theService.schedule( new SocketCloser(theSocket, theRetryDecider), aTimeoutInSeconds, TimeUnit.SECONDS );
        theWriter = new PrintWriter(new OutputStreamWriter(theSocket.getOutputStream()));
        theReader = new BufferedReader(new InputStreamReader(theSocket.getInputStream()));
//        LOGGER.debug( "Sending message: '" + aMessage + "'" );
//        System.out.println("SENDING MESSEGE IN PEERSENDER: '" + aMessage + "'");
        theWriter.println(aMessage);
        theWriter.flush();
        //stop the socketcloser at this point, otherwise it might close the socket during the next statements
        //and cause the message to be resent while it has already been delivered
        //        theService.shutdownNow();
        aPeerMessage.setState( State.SEND );

        //if we get here we have successfully created a socket and successfully send a message to the peer
        //it might be that the socket closer still times out because the other peer does not want to respond
        //in that case we should not retry because the same effect will probably result and retrying
        //causes the p2p network to be flooded with sockets
        //        theRetries = 0;

        theRetryDecider.messageSend();
        String theReturnMessage = theReader.readLine();
//        System.out.println("REPLY RECEIVED IN PEERSENDER: '" + theReturnMessage + "'");
//        LOGGER.debug( "Reply for input '" + aMessage + "' reply: '" + theReturnMessage + "'" );
        //just some code to make peers not using a streamsplitter compatible with those who use one
        if(theReturnMessage != null && theReturnMessage.startsWith(StreamSplitterPool.ID_PREFIX)){
          theReturnMessage = theReader.readLine();
        }
        //        LOGGER.debug( "Message received: '" + theReturnMessage + "'" );
        //TODO why do we sometimes have null replies when using BasicSocketPool
        if(theReturnMessage == null || "".equals( theReturnMessage )) {
          //          theRetries = 1;
          throw new IOException("empty result, socket corrupt?");
        }
        
        if(theReturnMessage.startsWith( StreamSplitter.OUT )){
          theReturnMessage = theReturnMessage.substring( StreamSplitter.OUT.length() );
        }

        SimpleNetworkInterface theNetworkInterface = NetTools.getNetworkInterfaceForLocalIP( ((InetSocketAddress)theSocket.getLocalSocketAddress()).getAddress().getHostAddress() );
        return new PeerSenderReply( theReturnMessage, theNetworkInterface);
      }catch(IOException e){
        //for some reason the socket was corrupt just close the socket and retry untill retry counter is zero
        P2PSettings.getInstance().getSocketPool().close( theSocket );
        if(!theRetryDecider.hasRetry()) {
          throw e;
        }
      }finally{
        theService.shutdownNow();
        P2PSettings.getInstance().getSocketPool().checkIn( theSocket );
      }
    }
    throw new IOException("Could not send message");
  }

  private class SocketCloser extends NamedRunnable{
    private final SocketProxy mySocket;
    private final RetryDecider myRetryDecider;

    public SocketCloser ( SocketProxy anSocket, RetryDecider aRetryDecier ) {
      super();
      mySocket = anSocket;
      myRetryDecider = aRetryDecier;
    }

    public void doRun(){
      myRetryDecider.timeoutOccured();
      P2PSettings.getInstance().getSocketPool().close(  mySocket );
    }
  }

  private class RetryDecider{
    private int myRetries = 3;
    private boolean isSocketCreated = false;
    private boolean isTimeoutOccured = false;
    private boolean isMessageSend = false;

    public RetryDecider(int aRetries){
      myRetries = aRetries;
    }

    public boolean retry(){
      boolean isHasRetry = hasRetry();
      myRetries--;
      return isHasRetry;
    }

    public boolean hasRetry(){
      if(isSocketCreated && isMessageSend && isTimeoutOccured) return false;
      return myRetries > 0;

    }

    public void timeoutOccured(){
      isTimeoutOccured = true;
    }

    public void socketCreated(){
      isSocketCreated = true;
    }

    public void messageSend(){
      isMessageSend = true;
    }

    public void exceptionOccured(Exception anException){

    }

    public void clear(){
      isSocketCreated = false;
      isMessageSend = false;
      isTimeoutOccured = false;
    }
  }
}
