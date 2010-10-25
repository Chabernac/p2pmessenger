/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SocketPoolCleanUpDecorator implements iSocketPool {

  private final iSocketPool mySocketPool;

  public SocketPoolCleanUpDecorator ( iSocketPool aSocketPool, long aTimeout, TimeUnit aTimeUnit ) {
    super();
    mySocketPool = aSocketPool;
    scheduleCleanUp(aTimeout, aTimeUnit);
  }
  
  private void scheduleCleanUp(final long aTimeout, final TimeUnit aTimeUnit){
    ScheduledExecutorService theService = Executors.newScheduledThreadPool( 1 );
    theService.scheduleAtFixedRate( new Runnable(){
      public void run(){
        cleanUpOlderThan( System.currentTimeMillis() - aTimeUnit.toMillis( aTimeout ) );
      }
    }, aTimeout, aTimeout, aTimeUnit);
  }

  public SocketProxy checkOut( SocketAddress anAddress ) throws IOException {
    return mySocketPool.checkOut( anAddress );
  }

  public void checkIn( SocketProxy aSocket ) {
    mySocketPool.checkIn( aSocket );
  }

  public void close( SocketProxy aSocket ) {
    mySocketPool.close( aSocket );
  }

  public void cleanUp() {
    mySocketPool.cleanUp();
  }

  public void cleanUpOlderThan( long aTimestamp ) {
    mySocketPool.cleanUpOlderThan( aTimestamp );
  }

  public List< SocketProxy > getCheckedInPool() {
    return mySocketPool.getCheckedInPool();
  }

  public List< SocketProxy > getCheckedOutPool() {
    return mySocketPool.getCheckedOutPool();
  }

  public List< SocketProxy > getConnectingPool() {
    return mySocketPool.getConnectingPool();
  }
  
  
}
