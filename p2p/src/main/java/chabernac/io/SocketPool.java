/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SocketPool extends Observable{
  private List< Socket > myCheckedInPool = Collections.synchronizedList( new ArrayList< Socket >());
  private List< Socket > myCheckedOutPool = Collections.synchronizedList( new ArrayList< Socket >());

  private static SocketPool INSTANCE = null; 

  private ScheduledExecutorService myService = null;


  private SocketPool(){
  }

  private void notifyAllObs(){
    setChanged();
    notifyObservers();
  }

  public synchronized static SocketPool getInstance(){
    if(INSTANCE == null){
      INSTANCE = new SocketPool();
    }
    return INSTANCE;
  }

  public void setCleanUpTimeInSeconds(int aCleanUpTimeoutInSeconds){
    if(myService != null) myService.shutdownNow();
    if(aCleanUpTimeoutInSeconds > 0){
      myService = Executors.newScheduledThreadPool(1);
      myService.scheduleAtFixedRate( 
          new Runnable(){
            public void run(){
              cleanUp();
            }
          }, 
          aCleanUpTimeoutInSeconds, 
          aCleanUpTimeoutInSeconds, 
          TimeUnit.SECONDS);
    }
  }

  private Socket searchFirstSocketWithAddressInPool(SocketAddress anAddress, List<Socket> aPool){
    for(Socket theSocket : aPool){
      if(theSocket.getRemoteSocketAddress().equals( anAddress )){
        return theSocket;
      }
    }
    return null;
  }

  public Socket checkOut(SocketAddress anAddress) throws IOException{
    Socket theSocket = searchFirstSocketWithAddressInPool( anAddress, myCheckedInPool);
    if(theSocket != null){
      synchronized(this){
        myCheckedInPool.remove( theSocket );
        myCheckedOutPool.add( theSocket );
        return theSocket;
      }
    }

    theSocket = new Socket();
    theSocket.connect( anAddress );
    myCheckedOutPool.add( theSocket );
    notifyAllObs();
    return theSocket;
  }

  public void checkIn(Socket aSocket){
    if(aSocket != null){
      synchronized(this){
        myCheckedOutPool.remove( aSocket );
        myCheckedInPool.add(aSocket);
      }
      notifyAllObs();
    }
  }

  public synchronized void close(Socket aSocket){
    try {
      aSocket.close();
    } catch ( IOException e ) {
    }
    myCheckedInPool.remove( aSocket );
    myCheckedOutPool.remove( aSocket );
    notifyAllObs();
  }

  public synchronized void cleanUp(){
    for(Socket theSocket : myCheckedInPool){
      try {
        theSocket.close();
      } catch ( IOException e ) {
      }
    }
    myCheckedInPool.clear();
    notifyAllObs();
  }

  List< Socket > getCheckInPool(){
    return Collections.unmodifiableList(  myCheckedInPool );
  }

  List< Socket > getCheckOutPool(){
    return Collections.unmodifiableList(  myCheckedOutPool );
  }
}
