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
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;

import chabernac.io.StreamSplitterPool.Result;
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
  private final String LOCK_PREFIX = UUID.randomUUID().toString();
  //  private AtomicInteger myOutgoingSocketCounter = new AtomicInteger(0);
  private Random myRandom = new Random();

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

  public void notifyStarted() throws StreamSplittingServerException{
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
    myExecutorService = new DynamicSizeExecutor( 1, 128, 0);
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

  public void kill() {
    if(myServerSocket != null){
      try{
        myServerSocket.close();
      }catch(Exception e){
        LOGGER.error("An error occured while closing server socket", e);
      }
    }
    if(myExecutorService != null)  myExecutorService.shutdownNow();
    myExecutorService = null;

    if(myPool != null) myPool.closeAll();
  }

  public synchronized void close(){
    try{
      myInputOutputHandler.close();
    }catch(Exception e){
      LOGGER.error( "Error occured while closing input output handler", e );
    }

    kill();
  }

  public boolean containsSocketForId(String anId){
    return myPool.contains(anId);
  }

  public boolean isStarted(){
    return isRunning;
  }

  private String addSocket(final Socket aSocket) throws IOException{
    //    LOGGER.debug("adding socket in server with id '" + myId + "' trying to add stream splitter");
    final StreamSplitter theSplitter = new StreamSplitter( aSocket.getInputStream(), aSocket.getOutputStream(), myInputOutputHandler );
    Result theResult = myPool.add( theSplitter );
    //    LOGGER.debug("Stream splitter added in server with id '" + myId + "' for remote server with id '" + theSplitter.getId() + "' result: " + theResult);

    if(theResult == Result.ADDED){
      theSplitter.addStreamListener( new iStreamListener() {
        @Override
        public void streamClosed() {
          try {
            LOGGER.debug( "Closing socket for peer id '" + theSplitter.getId() + "' because the stream was closed" );
            aSocket.close();
            mySockets.remove(theSplitter.getId());
          } catch ( IOException e ) {
            LOGGER.error( "Could not close socket", e );
          } 
        }
      });

      mySockets.put( theSplitter.getId(), aSocket );
    } else {
      aSocket.close();
    }
    return theSplitter.getId();

  }

  public String getRemoteId(String aHost, int aPort) throws IOException{
    return createSocket( aHost, aPort );
  }


  public SocketSenderReply send(String aHost, int aPort, String aMessage) throws IOException{
    return send(null, aHost, aPort, aMessage);
  }

  public String send(String anId, String aMessage) throws IOException{
    return send(anId, null, -1, aMessage).getReply();
  }

  private String createSocket(String aHost, int aPort) throws IOException {
    Result theResult = Result.CONCURRENT_CONNECTION_ATTEMPT;

    StreamSplitter theSplitter = null;
    Socket theSocket = null;
    int theCount = 0;

    while(theResult == Result.CONCURRENT_CONNECTION_ATTEMPT){
      /*
      try {
        if(theCount++ > 0){
          long theSleepTime =  Math.abs(myRandom.nextLong()) %  (theCount * 2);
          LOGGER.debug("Connection attempt '" + theCount + "' Sleeping '" + theSleepTime  + "' ms"); 
          Thread.sleep(  theSleepTime );
        }
      } catch ( InterruptedException e ) {
        LOGGER.error("could not sleep", e);
      }
      */
      LOGGER.debug( "Connection attempt '" + ++theCount  + "'");
      if(theSocket != null)  theSocket.close();
      theSocket = new Socket(aHost, aPort);
      theSocket.setReuseAddress( true );
      theSplitter = new StreamSplitter( theSocket.getInputStream(), theSocket.getOutputStream(), myInputOutputHandler );
      theResult = myPool.add( theSplitter );
    }
    
    LOGGER.debug( "Connection attempt '" + theCount + "' success!" );

    final StreamSplitter theSplit = theSplitter;
    final Socket theSock = theSocket;

    if(theResult == Result.ADDED){
      theSplitter.addStreamListener( new iStreamListener() {
        @Override
        public void streamClosed() {
          try {
            LOGGER.debug( "Closing socket for peer id '" + theSplit.getId() + "' because the stream was closed" );
            theSock.close();
            mySockets.remove(theSplit.getId());
          } catch ( IOException e ) {
            LOGGER.error( "Could not close socket", e );
          } 
        }
      });

      mySockets.put( theSplitter.getId(), theSock );
    } else {
      theSock.close();
    }

    return theSplit.getId();
  }

  private SocketSenderReply send(String anId, String aHost, int aPort, String aMessage) throws IOException{
    if(anId != null && anId.equals(myPool.getId())){
      return new SocketSenderReply( myInputOutputHandler.handle(anId, aMessage), anId);
    }

    String theLock = LOCK_PREFIX + anId;
    if(anId == null) theLock = aHost + ":" + aPort;

    synchronized(theLock){
      if(anId == null || !myPool.contains( anId )){
        try{
          anId = createSocket( aHost, aPort );
        } catch ( IOException e ) {
          LOGGER.error("Could not create socket", e);
        }
      }
      
      if(anId == null){
        throw new IOException("Unable to connect to '" + aHost + ":" + aPort);
      }
      
      if(anId.equals( myPool.getId() )){
        return new SocketSenderReply(myInputOutputHandler.handle(anId, aMessage), anId);
      } else if(myPool.contains( anId )){
        return new SocketSenderReply(myPool.send( anId, aMessage ), anId);
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
            /*
            if(myOutgoingSocketCounter.get() > 0){
              theSocket.getOutputStream().write(0);
              theSocket.getOutputStream().flush();
              theSocket.close();
            } else {
              theSocket.getOutputStream().write(1);
              theSocket.getOutputStream().flush();
              LOGGER.debug("Socket accepted in server with id '" + myId + "'");
            }
             */
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
