/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Observer;

import org.apache.log4j.Logger;

import chabernac.utils.ObservableList;

public class BasicSocketPool implements iSocketPool{
  private static Logger LOGGER = Logger.getLogger(BasicSocketPool.class);
  private ObservableList<SocketProxy> myCheckedOutPool = new ObservableList<SocketProxy>( new ArrayList< SocketProxy >() ) ;
  private ObservableList<SocketProxy> myConnectingPool = new ObservableList<SocketProxy>( new ArrayList< SocketProxy >() );
  private ObservableList<SocketProxy> myCheckedInPool = new ObservableList<SocketProxy>( new ArrayList< SocketProxy >() );

  private Object LOCK = new Object();

  private int myMaxAllowSocketsPerSocketAddress = 2;
  private int myMaxPeekSocketsPerAddress = 5;

  private boolean isSocketReuse = true;

  @Override
  public SocketProxy checkOut( SocketAddress anAddress ) throws IOException{
    LOGGER.debug("Trying to checkout connection for '" + anAddress + "'");
    LOGGER.debug("Socket for this address checked out " + countProxyForSocketAddressInPool( myCheckedOutPool, anAddress ));
    synchronized(LOCK){
      while(countProxyForSocketAddressInPool( myCheckedOutPool, anAddress ) >= myMaxPeekSocketsPerAddress){
        try {
          LOGGER.debug("Waiting for free socket " + countProxyForSocketAddressInPool( myCheckedOutPool, anAddress ));
          LOCK.wait();
        } catch (InterruptedException e) {
          LOGGER.error("Could not wait", e);
        }
      }
      
      SocketProxy theProxy = searchProxyForSocketAddressInPool( myCheckedInPool, anAddress);
      if(theProxy != null){
        theProxy.connect();
        if(!theProxy.isBound() || 
            theProxy.isClosed() ||
            !theProxy.isConnected() ||
            theProxy.isInputShutdown() ||
            theProxy.isOutputShutdown()){
          close( theProxy );
          LOGGER.debug("Closing socket "  + theProxy.getSocketAddress());
        } else {
          myCheckedInPool.remove( theProxy );
          myCheckedOutPool.add( theProxy );
          //this is a socket that will be shortly used so update the connect time
          theProxy.setConnectTime( new Date() );
          LOGGER.debug("returning existing socket " + theProxy.getSocketAddress());
          return theProxy;
        }
      }
    }

    LOGGER.debug("creating new socket " + anAddress);
    SocketProxy theSocket = new SocketProxy(anAddress);

    synchronized(LOCK){
      myConnectingPool.add( theSocket );
    }

    try{
      theSocket.connect();
    }catch(IOException e){
      synchronized(LOCK){
        myConnectingPool.remove( theSocket );
      }
      throw e;
    }

    synchronized(LOCK){
      myConnectingPool.remove( theSocket );
      myCheckedOutPool.add(theSocket);
    }

    return theSocket;
  }

  private SocketProxy searchProxyForSocketAddressInPool(List<SocketProxy> aPool, SocketAddress aSocketAddress){
    for(SocketProxy theProxy : myCheckedInPool){
      if(theProxy.getSocketAddress().equals( aSocketAddress )){
        return theProxy;
      }
    }
    return null;
  }

  private int countProxyForSocketAddressInPool(List<SocketProxy> aPool, SocketAddress aSocketAddress){
    int theCounter = 0;
    for(SocketProxy theProxy : myCheckedInPool){
      if(theProxy.getSocketAddress().equals( aSocketAddress )){
        theCounter ++;
      }
    }
    return theCounter;
  }

  @Override
  public void checkIn( SocketProxy aSocket ) {
    if(aSocket == null) return;

    if(!isSocketReuse) {
      close(aSocket);
    } else {
      synchronized(LOCK){
        int theProxiesForSocketAddress = countProxyForSocketAddressInPool( myCheckedInPool, aSocket.getSocketAddress() );

        myCheckedOutPool.remove( aSocket );

        if(theProxiesForSocketAddress >= 2){
          //only allow 2 sockets to be stored in the pool per address
          LOGGER.debug("Closing socket because max 2 entries can be stored in checked in pool " + aSocket.getSocketAddress());
          aSocket.close();
        } else {
          myCheckedInPool.add( aSocket );
        }
        
        LOCK.notifyAll();
      }
    }
  }

  @Override
  public void close( SocketProxy aSocket ) {
    if(aSocket == null) return;

    LOGGER.debug("Closing socket " + aSocket);
    aSocket.close();
    synchronized(LOCK){
      myCheckedOutPool.remove( aSocket );
      myCheckedInPool.remove(aSocket);
      LOCK.notifyAll();
    }
  }

  private void clean(List<SocketProxy> aPool){
    synchronized (LOCK) {
      for(SocketProxy theSocket : aPool){
        theSocket.close();
      }
      aPool.clear();
    }
  }

  @Override
  public void cleanUp() {
    clean(myCheckedInPool);
    clean(myCheckedOutPool);
    clean(myConnectingPool);
  }

  @Override
  public void cleanUpOlderThan( long aTimestamp ) {
    synchronized(LOCK){
      for(Iterator< SocketProxy > i = myCheckedInPool.iterator();i.hasNext();){
        SocketProxy theProxy = i.next();
        if(theProxy.getConnectTime().getTime() < aTimestamp){
          LOGGER.debug( "Cleaning up socket '"  + theProxy + "'");
          theProxy.close();
          i.remove();
        }
      }
    }
  }

  @Override
  public List< SocketProxy > getCheckedInPool() {
    return Collections.unmodifiableList( myCheckedInPool );
  }

  @Override
  public List< SocketProxy > getCheckedOutPool() {
    return Collections.unmodifiableList( myCheckedOutPool );
  }

  @Override
  public List< SocketProxy > getConnectingPool() {
    return Collections.unmodifiableList( myConnectingPool );
  }

  public int getMaxAllowSocketsPerSocketAddress() {
    return myMaxAllowSocketsPerSocketAddress;
  }

  public void setMaxAllowSocketsPerSocketAddress( int aMaxAllowSocketsPerSocketAddress ) {
    myMaxAllowSocketsPerSocketAddress = aMaxAllowSocketsPerSocketAddress;
  }

  public boolean isSocketReuse() {
    return isSocketReuse;
  }

  public void setSocketReuse( boolean aSocketReuse ) {
    isSocketReuse = aSocketReuse;
  }
  
  public void addObserver(Observer anObserver){
    myCheckedInPool.addObserver( anObserver );
    myCheckedOutPool.addObserver( anObserver );
    myConnectingPool.addObserver( anObserver );
  }
  
  public void deleteObserver(Observer anObserver){
    myCheckedInPool.deleteObserver( anObserver );
    myCheckedOutPool.deleteObserver( anObserver );
    myConnectingPool.deleteObserver( anObserver );
  }
}
