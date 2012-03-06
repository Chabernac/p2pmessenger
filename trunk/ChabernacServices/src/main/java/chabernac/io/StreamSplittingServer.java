/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;

import chabernac.thread.DynamicSizeExecutor;
import chabernac.utils.NamedRunnable;
import chabernac.utils.NetTools;

public class StreamSplittingServer implements iSocketSender{
  private static final Logger LOGGER = Logger.getLogger(StreamSplittingServer.class);
  private final iInputOutputHandler myInputOutputHandler;
  private ExecutorService myExecutorService = null;
  private final int myPort;
  private final boolean isFindUnusedPort;
  private ServerSocket myServerSocket;
  private StreamSplitterPool myPool;
  private boolean isRunning = false;
  private List<iStreamSplittingServerListener> myListeners = new ArrayList< iStreamSplittingServerListener >();
  private Map<String, Socket> mySockets = new HashMap<String, Socket>();
  private final String myId;

  public StreamSplittingServer ( iInputOutputHandler aInputOutputHandler, int aPort, boolean isFindUnusedPort, String anId ) {
    super();
    myInputOutputHandler = aInputOutputHandler;
    myPort = aPort;
    this.isFindUnusedPort = isFindUnusedPort;
    myId = anId;
  }

  public void addListener(iStreamSplittingServerListener aListener){
    myListeners.add(aListener);
  }

  public void removeListener(iStreamSplittingServerListener aListener){
    myListeners.remove(aListener);
  }

  public void notifyStarted(){
    for(iStreamSplittingServerListener theListener : myListeners){
      theListener.streamSplittingServerStarted( myServerSocket.getLocalPort(), this );
    }
  }

  public void notifyStopped(){
    for(iStreamSplittingServerListener theListener : myListeners){
      theListener.streamSplittingServerStopped();
    }
  }

  public synchronized boolean start(){
    if(isRunning) return false;
    isRunning = true;
    myExecutorService = new DynamicSizeExecutor( 1, 128);
    myPool = new StreamSplitterPool(myId, myExecutorService);
    CountDownLatch theCountdownLatch = new CountDownLatch( 1 );
    myExecutorService.execute( new ServerThread(theCountdownLatch, myExecutorService) );
    try{
      theCountdownLatch.await();
    }catch(InterruptedException e){
      LOGGER.error("Could not wait for countdown latch", e);
    }
    return isRunning;
  }

  public synchronized void close(){
    try{
      myInputOutputHandler.close();
    }catch(Exception e){
      LOGGER.error( "Error occured while closing input output handler", e );
    }

    if(myServerSocket != null){
      try{
        myServerSocket.close();
      }catch(Exception e){
        LOGGER.error("An error occured while closing server socket", e);
      }
    }
    if(myExecutorService != null)  myExecutorService.shutdownNow();
    myExecutorService = null;

    myPool.closeAll();
  }

  public boolean containsSocketForId(String anId){
    return myPool.contains(anId);
  }

  public boolean isStarted(){
    return isRunning;
  }

  private String addSocket(final Socket aSocket) throws IOException{
    StreamSplitter theSplitter = new StreamSplitter( aSocket.getInputStream(), aSocket.getOutputStream(), myInputOutputHandler );
    final String theId = myPool.add( theSplitter );

    theSplitter.addStreamListener( new iStreamListener() {
      @Override
      public void streamClosed() {
        try {
          aSocket.close();
          mySockets.remove(theId);
        } catch ( IOException e ) {
          LOGGER.error( "Could not close socket", e );
        } 
      }
    });

    mySockets.put( theId, aSocket );
    return theId;

  }

  public String send(String anId, String aHost, int aPort, String aMessage) throws IOException{
    if(anId != null && anId.equals(myPool.getId())){
      return myInputOutputHandler.handle(anId, aMessage);
    }

    String theLock = anId;
    if(theLock == null) theLock = aHost + ":" + aPort;

    synchronized(theLock){
      if(anId == null || !myPool.contains( anId )){
        anId = addSocket( new Socket(aHost, aPort) );
      }
      if(myPool.contains( anId )){
        return myPool.send( anId, aMessage );
      }
      throw new IOException("No socket present for id '" + anId + "'");
    }
  }

  private class ServerThread extends NamedRunnable{
    private final ExecutorService myCurrentExecutorService;
    private final CountDownLatch myServerThreadCounter;


    public ServerThread ( CountDownLatch aCountDownLathc, ExecutorService aCurrentExecutorService ) {
      super();
      myCurrentExecutorService = aCurrentExecutorService;
      myServerThreadCounter = aCountDownLathc;
    }


    @Override
    protected void doRun() {
      try{
        if(isFindUnusedPort){
          myServerSocket = NetTools.openServerSocket( myPort );
        } else {
          myServerSocket = new ServerSocket(myPort);
        }

        notifyStarted();
        myServerThreadCounter.countDown();

        while(myExecutorService == myCurrentExecutorService){
          Socket theSocket = null;
          try{
            theSocket = myServerSocket.accept();
            addSocket( theSocket );
          }catch(Exception e){
            LOGGER.error("Could not add server socket", e);
            if(theSocket != null){
              theSocket.close();
            }
          }
        }
      }catch(Exception e){
        LOGGER.error("Error occured in server thread", e);
      } finally {
        isRunning = false;
        myServerThreadCounter.countDown();
        notifyStopped();
      }
    }
  }

  @Override
  public Socket getSocket( String anId ) {
    return mySockets.get(anId);
  }
}
