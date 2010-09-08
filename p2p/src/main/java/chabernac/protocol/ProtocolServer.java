/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import chabernac.tools.NetTools;
import chabernac.util.concurrent.MonitorrableRunnable;
import chabernac.util.concurrent.iRunnableListener;

public class ProtocolServer implements Runnable{
  private static Logger LOGGER = Logger.getLogger(ProtocolServer.class);

  private Random myRandom = new Random();

  private int myPort;
  private int myNumberOfThreads;
  private IProtocol myProtocol = null;
  private boolean isStarted = false;
  private ServerSocket myServerSocket = null;
  private boolean isFindUnusedPort = false;
  private ServerInfo myServerInfo = new ServerInfo();

  private Object LOCK = new Object();
  private AtomicLong mySimultanousThreads = new AtomicLong();
  private iRunnableListener myRunnableListener = null;

  public ProtocolServer(IProtocol aProtocol, int aPort, int aNumberOfThreads){
    this(aProtocol, aPort, aNumberOfThreads, false);
  }

  public ProtocolServer(IProtocol aProtocol, int aPort, int aNumberOfThreads, boolean isFindUnusedPort){
    this.isFindUnusedPort = isFindUnusedPort;
    myProtocol = aProtocol;
    myPort = aPort;
    myNumberOfThreads = aNumberOfThreads;
  }

  public boolean start(){
    if(isStarted) return isStarted;
    synchronized(LOCK){
      new Thread(this).start();
      try{
        if(!isStarted) LOCK.wait();
      }catch(InterruptedException e){
        e.printStackTrace();
      }
    }
    return isStarted;
  }

  public void stop(){
    try {
      if(myServerSocket != null){
        myServerSocket.close();
      }
    } catch ( IOException e ) {
    }
    myProtocol.stop();

    int theCount = 5;
    while(isStarted && theCount-- > 0){
      synchronized ( LOCK ) {
        try {
          LOCK.wait(1000);
        } catch ( InterruptedException e ) {
        }
      }
    }
  }

  @Override
  public void run() {
    try{
      mySimultanousThreads.incrementAndGet();

      if(isFindUnusedPort){
        myServerSocket = NetTools.openServerSocket( myPort );
      } else {
        myServerSocket = new ServerSocket(myPort);
      }

      synchronized ( LOCK ) {
        isStarted = true;
        LOCK.notify();
      }

      myServerInfo.setServerPort( myServerSocket.getLocalPort() );
      myProtocol.setServerInfo( myServerInfo );

      ExecutorService theClientHandlerService = Executors.newFixedThreadPool( myNumberOfThreads );

      LOGGER.debug( "Starting protocol server at port '" + myPort + "'" );

      while(true){ 
        Socket theClientSocket = myServerSocket.accept();
//        LOGGER.debug("Client accepted, current number of clients: " + mySimultanousThreads.get());
        ClientSocketHandler theHandler = new ClientSocketHandler(theClientSocket);
        if(myRunnableListener != null){
          theHandler.addListener( myRunnableListener );
        }
        theClientHandlerService.execute( theHandler );
      }
    }catch(SocketException e){
      if(!"socket closed".equalsIgnoreCase( e.getMessage())){
        LOGGER.error("Could not start server", e);
      }
    }catch(Exception e){
      LOGGER.error("Could not start server", e);
    } finally {
      synchronized ( LOCK ) {
        isStarted = false;
        LOCK.notify();
      }
      mySimultanousThreads.decrementAndGet();
    }
  }

  public boolean isStarted(){
    return isStarted;
  }

  public iRunnableListener getRunnableListener() {
    return myRunnableListener;
  }

  public void setRunnableListener( iRunnableListener anRunnableListener ) {
    myRunnableListener = anRunnableListener;
  }


  private class ClientSocketHandler extends MonitorrableRunnable{
    private Socket mySocket = null;


    public ClientSocketHandler(Socket aSocket){
      mySocket = aSocket;
    }

    public void doRun(){
      long theSessionId = myRandom.nextLong();

      BufferedReader theReader = null;
      PrintWriter theWriter = null;
      try{
        theReader = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
        theWriter = new PrintWriter(new OutputStreamWriter(mySocket.getOutputStream()));

        String theLine = null;
        while( (theLine = theReader.readLine()) != null){
//          LOGGER.debug("Line received: '" + theLine + "'");
          String  theResult = myProtocol.handleCommand( theSessionId, theLine );
//          LOGGER.debug("Sending result: '" + theResult + "'");
          theWriter.println( theResult );
          theWriter.flush();
        }
      }catch(IOException e){
        LOGGER.error( "Io exception occured in protocol server", e );
      } finally {
        try{
          theWriter.flush();
          theWriter.close();
          theReader.close();
        }catch(IOException e){
          LOGGER.error( "Could not close streams", e );
        }
        try {
          mySocket.close();
        } catch ( IOException e ) {
          LOGGER.error( "Could not close socket", e );
        }
      }
    }

    @Override
    protected String getExtraInfo() {
      return mySocket.getInetAddress().getHostName() + ":" + mySocket.getPort();
    }

    public ServerInfo getServerInfo(){
      return myServerInfo;
    }
  }
}
