/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;

import chabernac.thread.DynamicSizeExecutor;
import chabernac.utils.NamedRunnable;
import chabernac.utils.NetTools;

public class StreamSplittingServer {
  private static final Logger LOGGER = Logger.getLogger(StreamSplittingServer.class);
  private final iInputOutputHandler myInputOutputHandler;
  private ExecutorService myExecutorService = null;
  private final int myPort;
  private final boolean isFindUnusedPort;
  private ServerSocket myServerSocket;
  private final StreamSplitterPool myPool;

  public StreamSplittingServer ( iInputOutputHandler aInputOutputHandler, int aPort, boolean isFindUnusedPort, String anId ) {
    super();
    myInputOutputHandler = aInputOutputHandler;
    myPort = aPort;
    this.isFindUnusedPort = isFindUnusedPort;
    myPool = new StreamSplitterPool( anId );
  }

  public void start(){
    myExecutorService = new DynamicSizeExecutor( 1, 128);
    myExecutorService.execute( new ServerThread(myExecutorService) );
  }

  public void close(){
    myExecutorService.shutdownNow();
    myExecutorService = null;
  }
  
  private void addSocket(final Socket aSocket){
    try{
      StreamSplitter theSplitter = new StreamSplitter( aSocket.getInputStream(), aSocket.getOutputStream(), myInputOutputHandler );
      theSplitter.addStreamListener( new iStreamListener() {
        @Override
        public void streamClosed() {
          try {
            aSocket.close();
          } catch ( IOException e ) {
            LOGGER.error( "Could not close socket", e );
          } 
        }
      });
      myPool.add( theSplitter );
    }catch(Exception e){
      LOGGER.error("An error occured while adding socket", e);
    }
  }
  
  public void send(String anId, String aHost, int aPort, String aMessage) throws IOException{
    synchronized(anId){
      if(!myPool.contains( anId )){
        addSocket( new Socket(aHost, aPort) );
      }
      if(myPool.contains( anId )){
        myPool.send( anId, aMessage );
      }
    }
  }

  private class ServerThread extends NamedRunnable{
    private final ExecutorService myCurrentExecutorService;


    public ServerThread ( ExecutorService aCurrentExecutorService ) {
      super();
      myCurrentExecutorService = aCurrentExecutorService;
    }


    @Override
    protected void doRun() {
      try{
        if(isFindUnusedPort){
          myServerSocket = NetTools.openServerSocket( myPort );
        } else {
          myServerSocket = new ServerSocket(myPort);
        }

        while(myExecutorService == myCurrentExecutorService){
          Socket theSocket = myServerSocket.accept();
          addSocket( theSocket );
        }
      }catch(Exception e){
        LOGGER.error("Error occured in server thread", e);
      }
    }
  }
}
