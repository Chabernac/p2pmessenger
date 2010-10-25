/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CachingSocketPool extends Observable implements iSocketPool{
  private Pool< SocketProxy > myCheckedInPool = new Pool< SocketProxy >();
  private Pool< SocketProxy > myCheckedOutPool = new Pool< SocketProxy >();
  private Pool< SocketProxy > myConnectingPool = new Pool< SocketProxy >();

  private ScheduledExecutorService myService = null;

  //default clean timeout is 30 seconds
  private long myCleanUpTimeoutInSeconds = 30;


  protected CachingSocketPool(){
  }

  private void notifyAllObs(){
    setChanged();
    notifyObservers();
  }

  public void setCleanUpTimeInSeconds(int aCleanUpTimeoutInSeconds){
    if(myService != null) myService.shutdownNow();
    if(aCleanUpTimeoutInSeconds > 0){
      myCleanUpTimeoutInSeconds = aCleanUpTimeoutInSeconds;
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

  private SocketProxy searchFirstSocketWithAddressInPool(SocketAddress anAddress, Pool<SocketProxy> aPool){
    for(SocketProxy theSocket : aPool){
      if(theSocket.getSocketAddress().equals( anAddress )){
        return theSocket;
      }
    }
    return null;
  }

  public SocketProxy checkOut(SocketAddress anAddress) throws IOException{
    SocketProxy theSocketProxy = null;
    while((theSocketProxy  = searchFirstSocketWithAddressInPool( anAddress, myCheckedInPool)) != null){
      synchronized(this){
        myCheckedInPool.remove( theSocketProxy );
        myCheckedOutPool.add( theSocketProxy );
        notifyAllObs();
        theSocketProxy.connect();
        return theSocketProxy;
      }
    }

//    theSocketProxy = searchFirstSocketWithAddressInPool( anAddress, myConnectingPool );
//    if(theSocketProxy != null){
//      //in this case some other thread also is trying to connect to the same address.  We will not allow two seperate threads
//      //to try to connect to the same host at the same port as it will start consuming to much resources after a while
//      //throw an exception
//      throw new IOException("Another process already tries to contact this host at this port");
//    }

    theSocketProxy = new SocketProxy(anAddress);
    myConnectingPool.add( theSocketProxy );
    notifyAllObs();
    try{
      theSocketProxy.connect( );
      myCheckedOutPool.add( theSocketProxy );
      return theSocketProxy;
    } finally{
      myConnectingPool.remove( theSocketProxy );
      notifyAllObs();
    }
  }

  public void checkIn(SocketProxy aSocket){
    if(aSocket != null){
      synchronized(this){
        myCheckedOutPool.remove( aSocket );
        myCheckedInPool.add(aSocket);
      }
      notifyAllObs();
    }
  }

  public synchronized void close(SocketProxy aSocket){
    if(aSocket == null) return;
    aSocket.close();
    myCheckedInPool.remove( aSocket );
    myCheckedOutPool.remove( aSocket );
    myConnectingPool.remove( aSocket );
    notifyAllObs();
  }

  private void cleanItemsOlderThan(Pool<SocketProxy> aPool, long aTime){
    for(SocketProxy theSocket : aPool.getItemsOlderThan( System.currentTimeMillis() - aTime * 1000 )){
      theSocket.close();
      aPool.remove( theSocket );
    }
  }

  /**
   * this method will clean up the items in the pools which are older then the clean up time in seconds.
   * This means items that have not been checked in or out of this pool during that time.
   */
  public synchronized void cleanUp(){
    cleanItemsOlderThan( myCheckedInPool,  myCleanUpTimeoutInSeconds);
    cleanItemsOlderThan( myCheckedOutPool,  myCleanUpTimeoutInSeconds);
    cleanItemsOlderThan( myConnectingPool,  myCleanUpTimeoutInSeconds);

    notifyAllObs();
  }

  /**
   * cleans up all items.
   * all the sockets in the pools are closed
   */
  public synchronized void fullClean(){
    cleanItemsOlderThan( myCheckedInPool,  -1);
    cleanItemsOlderThan( myCheckedOutPool,  -1);
    cleanItemsOlderThan( myConnectingPool,  -1);
  }

  public List< SocketProxy > getCheckedInPool(){
    return Collections.unmodifiableList(  myCheckedInPool.getItemsOlderThan( System.currentTimeMillis() ) );
  }

  public List< SocketProxy > getCheckedOutPool(){
    return Collections.unmodifiableList(  myCheckedOutPool.getItemsOlderThan( System.currentTimeMillis() ) );
  }

  public List< SocketProxy > getConnectingPool(){
    return Collections.unmodifiableList(  myConnectingPool.getItemsOlderThan( System.currentTimeMillis() ));
  }

  @Override
  public void cleanUpOlderThan( long aTimestamp ) {
  }
}
