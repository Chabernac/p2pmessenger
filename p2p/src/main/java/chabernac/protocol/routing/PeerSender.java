package chabernac.protocol.routing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import chabernac.io.SocketPoolFactory;
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

    Socket theSocket = aPeer.createSocket( aPeer.getPort() );

    if(theSocket == null) {
      changeState(theMessage, State.NOK);
      throw new IOException("Could not open socket to peer: " + aPeer.getPeerId() + " " + aPeer.getHosts() + ":" + aPeer.getPort());
    }

    ScheduledExecutorService theService = Executors.newScheduledThreadPool( 1 );

    BufferedReader theReader = null;
    PrintWriter theWriter = null;
    try{
      theService.schedule( new SocketCloser(theSocket), aTimeoutInSeconds, TimeUnit.SECONDS );
      theWriter = new PrintWriter(new OutputStreamWriter(theSocket.getOutputStream()));
      theReader = new BufferedReader(new InputStreamReader(theSocket.getInputStream()));
      theWriter.println(aMessage);
      theWriter.flush();
      changeState(theMessage, State.SEND);
      String theReturnMessage = theReader.readLine();
      theMessage.setResult(theReturnMessage);
      notifyListeners(theMessage);

      return theReturnMessage;
    }catch(IOException e){
      theMessage.setState(State.NOK);
      notifyListeners(theMessage);
      throw e;
    }finally{
      theService.shutdownNow();
      SocketPoolFactory.getSocketPool().checkIn( theSocket );
    }
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

  private class SocketCloser implements Runnable{
    private final Socket mySocket;

    public SocketCloser ( Socket anSocket ) {
      super();
      mySocket = anSocket;
    }

    public void run(){
      SocketPoolFactory.getSocketPool().close(  mySocket );
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

    try{
      theConnection = theCometURL.openConnection();
      theConnection.setDoOutput(true);
      theWriter = new OutputStreamWriter(theConnection.getOutputStream());
      theWriter.write("session=-1&input=" + URLEncoder.encode(aMessage, "UTF-8"));
      theWriter.flush();

      changeState(theMessage, State.SEND);

      theReader = new BufferedReader(new InputStreamReader(theConnection.getInputStream()));
      return theReader.readLine();
    } finally {
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
