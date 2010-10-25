/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

public class BasicSocketPool extends Observable implements iSocketPool{
//  private static Logger LOGGER = Logger.getLogger(BasicSocketPool.class);
  private List<SocketProxy> myCheckedOutPool = new ArrayList< SocketProxy >() ;
  private List<SocketProxy> myConnectingPool = new ArrayList< SocketProxy >() ;
  private List<SocketProxy> myCheckedInPool = new ArrayList< SocketProxy >() ;

  private Object LOCK = new Object();

  private int myMaxAllowSocketsPerSocketAddress = 2;

  @Override
  public SocketProxy checkOut( SocketAddress anAddress ) throws IOException{
    synchronized(LOCK){
      SocketProxy theProxy = searchProxyForSocketAddressInPool( myCheckedInPool, anAddress);
      if(theProxy != null){
        theProxy.connect();
        if(!theProxy.isBound() || 
            theProxy.isClosed() ||
            !theProxy.isConnected() ||
            theProxy.isInputShutdown() ||
            theProxy.isOutputShutdown()){
          close( theProxy );
        } else {
          myCheckedInPool.remove( theProxy );
          myCheckedOutPool.add( theProxy );
          return theProxy;
        }
      }
    }

    SocketProxy theSocket = new SocketProxy(anAddress);

    synchronized(LOCK){
      myConnectingPool.add( theSocket );
      notifyAllObs();
    }

    try{
      theSocket.connect();
    }catch(IOException e){
      synchronized(LOCK){
        myConnectingPool.remove( theSocket );
        notifyAllObs();
      }
      throw e;
    }

    synchronized(LOCK){
      myConnectingPool.remove( theSocket );
      myCheckedOutPool.add(theSocket);
      notifyAllObs();
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


    synchronized(LOCK){
      int theProxiesForSocketAddress = countProxyForSocketAddressInPool( myCheckedInPool, aSocket.getSocketAddress() );

      myCheckedOutPool.remove( aSocket );

      if(theProxiesForSocketAddress >= 2){
        //only allow 2 sockets to be stored in the pool per address
        aSocket.close();
      } else {
        myCheckedInPool.add( aSocket );
      }
      notifyAllObs();
    }
  }

  @Override
  public void close( SocketProxy aSocket ) {
    if(aSocket == null) return;
    
    aSocket.close();
    synchronized(LOCK){
      myCheckedOutPool.remove( aSocket );
      myCheckedInPool.remove(aSocket);
      notifyAllObs();
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
    notifyAllObs();
  }

  @Override
  public void cleanUpOlderThan( long aTimestamp ) {
    synchronized(LOCK){
      for(Iterator< SocketProxy > i = myCheckedInPool.iterator();i.hasNext();){
        SocketProxy theProxy = i.next();
        if(theProxy.getConnectTime().getTime() < aTimestamp){
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

  private void notifyAllObs(){
    setChanged();
    notifyObservers();
  }

  public int getMaxAllowSocketsPerSocketAddress() {
    return myMaxAllowSocketsPerSocketAddress;
  }

  public void setMaxAllowSocketsPerSocketAddress( int aMaxAllowSocketsPerSocketAddress ) {
    myMaxAllowSocketsPerSocketAddress = aMaxAllowSocketsPerSocketAddress;
  }
}
