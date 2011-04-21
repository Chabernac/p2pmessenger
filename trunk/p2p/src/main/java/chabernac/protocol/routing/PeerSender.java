package chabernac.protocol.routing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import chabernac.io.SocketProxy;
import chabernac.p2p.settings.P2PSettings;
import chabernac.protocol.routing.PeerMessage.State;

public class PeerSender implements iPeerSender {
  private static Logger LOGGER = Logger.getLogger(PeerSender.class);
  private boolean isKeepHistory = false;
  private List<PeerMessage> myHistory = new ArrayList<PeerMessage>();
  private List<iSocketPeerSenderListener> myPeerSenderListeners = new ArrayList<iSocketPeerSenderListener>();
  private long myBytesSend = 0;
  private long myBytesReceived = 0;
  private long myInitTime = System.currentTimeMillis();
  private String myPeerId;
  
  public PeerSender(){
    
  }
  
  public PeerSender(String aSendingPeer){
    myPeerId = aSendingPeer;
  }
  
  @Override
  public String send(String aMessage, SocketPeer aPeer, int aTimeoutInSeconds) throws IOException {
    PeerMessage theMessage = new PeerMessage(aMessage, aPeer);
    if(isKeepHistory) {
      myHistory.add(theMessage);
      notifyListeners(theMessage);
    }

    RetryDecider theRetryDecider = new RetryDecider(3);

    while(theRetryDecider.retry()){
      theRetryDecider.clear();
      SocketProxy theSocket = aPeer.createSocket( aPeer.getPort() );

      if(theSocket == null) {
        changeState(theMessage, State.NOK);
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
        theWriter.println(aMessage);
        theWriter.flush();
        myBytesSend += aMessage.length();
        //stop the socketcloser at this point, otherwise it might close the socket during the next statements
        //and cause the message to be resent while it has already been delivered
//        theService.shutdownNow();
        changeState(theMessage, State.SEND);
        
        //if we get here we have successfully created a socket and successfully send a message to the peer
        //it might be that the socket closer still times out because the other peer does not want to respond
        //in that case we should not retry because the same effect will probably result and retrying
        //causes the p2p network to be flooded with sockets
//        theRetries = 0;
        
        theRetryDecider.messageSend();
        String theReturnMessage = theReader.readLine();
//        LOGGER.debug( "Message received: '" + theReturnMessage + "'" );
        //TODO why do we sometimes have null replies when using BasicSocketPool
        if(theReturnMessage == null || "".equals( theReturnMessage )) {
//          theRetries = 1;
          throw new IOException("empty result, socket corrupt?");
        }
        myBytesReceived += theReturnMessage.length();
        theMessage.setResult(theReturnMessage);
        notifyListeners(theMessage);

        return theReturnMessage;
      }catch(IOException e){
        //for some reason the socket was corrupt just close the socket and retry untill retry counter is zero
        P2PSettings.getInstance().getSocketPool().close( theSocket );
        if(!theRetryDecider.hasRetry()) {
          theMessage.setState(State.NOK);
          notifyListeners(theMessage);
          throw e;
        }
      }finally{
        theService.shutdownNow();
        P2PSettings.getInstance().getSocketPool().checkIn( theSocket );
      }
    }
    throw new IOException("Could not send message");
  }
  
  private void changeState(PeerMessage aMessage, PeerMessage.State aState){
    aMessage.setState(aState);
    notifyListeners(aMessage);
  }


  private void notifyListeners(PeerMessage aMessage){
    for(iSocketPeerSenderListener theListener : myPeerSenderListeners){
      theListener.messageStateChanged(aMessage);
    }
  }

  public List<PeerMessage> getHistory(){
    return Collections.unmodifiableList(myHistory);
  }

  public boolean isKeepHistory() {
    return isKeepHistory;
  }

  public void setKeepHistory(boolean anIsKeepHistory) {
    isKeepHistory = anIsKeepHistory;
  }
  
  public void clearHistory(){
    myHistory.clear();
  }

  private class SocketCloser implements Runnable{
    private final SocketProxy mySocket;
    private final RetryDecider myRetryDecider;

    public SocketCloser ( SocketProxy anSocket, RetryDecider aRetryDecier ) {
      super();
      mySocket = anSocket;
      myRetryDecider = aRetryDecier;
    }

    public void run(){
      myRetryDecider.timeoutOccured();
      P2PSettings.getInstance().getSocketPool().close(  mySocket );
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
  
  

  public long getBytesSend() {
    return myBytesSend;
  }

  public long getBytesReceived() {
    return myBytesReceived;
  }

  public long getInitTime() {
    return myInitTime;
  }


  public void addPeerSenderListener(iSocketPeerSenderListener aListener){
    myPeerSenderListeners.add(aListener);
  }

  public void removePeerSenderListener(iSocketPeerSenderListener aListener){
    myPeerSenderListeners.remove(aListener);
  }

  @Override
  public String send(String aMessage, WebPeer aPeer, int aTimeout) throws IOException {
    if(myPeerId == null) throw new IOException("When sending to a web peer we need to have a sender peer id, maybe you forgot to call iPeerSender.setPeerId(String aPeerId) with the peer id of the sending peer...");
    
    PeerMessage theMessage = new PeerMessage(aMessage, aPeer);
    if(isKeepHistory) {
      myHistory.add(theMessage);
      notifyListeners(theMessage);
    }

    URL theCometURL = new URL(aPeer.getURL(), "p2p/protocol");
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
      theWriter.write( myPeerId );
      theWriter.write("&input=");
      theWriter.write(URLEncoder.encode(aMessage, "UTF-8"));
      theWriter.flush();

      changeState(theMessage, State.SEND);

      theReader = new BufferedReader(new InputStreamReader(theConnection.getInputStream()));
      String theResponse = theReader.readLine();
      theMessage.setResult( theResponse );
      notifyListeners( theMessage );
      return theResponse;
    }catch(IOException e){
      changeState(theMessage, State.NOK);
      notifyListeners(theMessage);
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



  @Override
  public String send( String aMessage, IndirectReachablePeer aIndirectReachablePeer, int aTimeoutInSeconds ) {
    // TODO Auto-generated method stub
    return null;
  }

  public String getPeerId() {
    return myPeerId;
  }

  public void setPeerId(String anPeerId) {
    myPeerId = anPeerId;
  }
}
