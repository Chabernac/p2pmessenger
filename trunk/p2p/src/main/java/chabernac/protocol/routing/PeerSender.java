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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import chabernac.io.SocketProxy;
import chabernac.p2p.settings.P2PSettings;
import chabernac.protocol.routing.PeerMessage.State;

public class PeerSender implements iPeerSender {
  private boolean isKeepHistory = false;
  private List<PeerMessage> myHistory = new ArrayList<PeerMessage>();
  private List<iSocketPeerSenderListener> myPeerSenderListeners = new ArrayList<iSocketPeerSenderListener>();
  
  @Override
  public String send(String aMessage, SocketPeer aPeer, int aTimeoutInSeconds) throws IOException {
    PeerMessage theMessage = new PeerMessage(aMessage, aPeer);
    if(isKeepHistory) {
      myHistory.add(theMessage);
      notifyListeners(theMessage);
    }

    int theRetries = 3;

    while(theRetries-- > 0){
      SocketProxy theSocket = aPeer.createSocket( aPeer.getPort() );

      if(theSocket == null) {
        changeState(theMessage, State.NOK);
        throw new IOException("Could not open socket to peer: " + aPeer.getPeerId() + " " + aPeer.getHosts() + ":" + aPeer.getPort());
      }

      ScheduledExecutorService theService = Executors.newScheduledThreadPool( 1 );

      BufferedReader theReader = null;
      PrintWriter theWriter = null;
      try{
        if(aTimeoutInSeconds > 0) theService.schedule( new SocketCloser(theSocket), aTimeoutInSeconds, TimeUnit.SECONDS );
        theWriter = new PrintWriter(new OutputStreamWriter(theSocket.getOutputStream()));
        theReader = new BufferedReader(new InputStreamReader(theSocket.getInputStream()));
        theWriter.println(aMessage);
        theWriter.flush();
        //stop the socketcloser at this point, otherwise it might close the socket during the next statements
        //and cause the message to be resent while it has already been delivered
//        theService.shutdownNow();
        changeState(theMessage, State.SEND);
        String theReturnMessage = theReader.readLine();
        //TODO why do we sometimes have null replies when using BasicSocketPool
        if(theReturnMessage == null || "".equals( theReturnMessage )) throw new IOException("empty result, socket corrupt?");
        theMessage.setResult(theReturnMessage);
        notifyListeners(theMessage);

        return theReturnMessage;
      }catch(IOException e){
        //for some reason the socket was corrupt just close the socket and retry untill retry counter is zero
        P2PSettings.getInstance().getSocketPool().close( theSocket );
        if(theRetries <= 0) {
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

    public SocketCloser ( SocketProxy anSocket ) {
      super();
      mySocket = anSocket;
    }

    public void run(){
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

  public void addPeerSenderListener(iSocketPeerSenderListener aListener){
    myPeerSenderListeners.add(aListener);
  }

  public void removePeerSenderListener(iSocketPeerSenderListener aListener){
    myPeerSenderListeners.remove(aListener);
  }

  @Override
  public String send(String aMessage, WebPeer aPeer, int aTimeout)throws IOException {
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
      theWriter.write("session=-1&input=" + URLEncoder.encode(aMessage, "UTF-8"));
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
}
