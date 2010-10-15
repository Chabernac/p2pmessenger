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

public class SimpleSocketPool extends Observable implements iSocketPool<SocketProxy>{
  private List<SocketProxy> myCheckedOutPool = new ArrayList< SocketProxy >() ;
  private List<SocketProxy> myConnectingPool = new ArrayList< SocketProxy >() ;

  private Object LOCK = new Object();

  @Override
  public Socket checkOut( SocketAddress anAddress ) throws IOException{
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

    return theSocket.getSocket();
  }

  private SocketProxy searchProxyForSocketInPool(List<SocketProxy> aPool, Socket aSocket){
    synchronized(LOCK){
      for(SocketProxy theProxy : aPool){
        if(theProxy.getSocket() == aSocket){
          return theProxy;
        }
      }
    }
    return null;
  }

  @Override
  public void checkIn( Socket aSocket ) {
    SocketProxy theProxy = searchProxyForSocketInPool( myCheckedOutPool, aSocket );
    
    if(theProxy == null) {
      try {
        aSocket.close();
      } catch ( IOException e ) {
      }
      return;
    }

    try {
      if(theProxy.getSocket() != null){
        aSocket.close();
      }
    } catch ( IOException e ) {
    } finally {
      synchronized(LOCK){
        myCheckedOutPool.remove( theProxy );
        notifyAllObs();
      }
    }
  }

  @Override
  public void close( Socket aSocket ) {
    //close and check in are the same in the simple pool
    checkIn( aSocket );
  }

  @Override
  public void cleanUp() {
    synchronized (LOCK) {
      for(SocketProxy theSocket : myCheckedOutPool){
        if(theSocket.getSocket() != null){
          try {
            theSocket.getSocket().close();
          } catch ( IOException e ) {
          }
        }
      }
      for(SocketProxy theSocket : myConnectingPool){
        if(theSocket.getSocket() != null){
          try {
            theSocket.getSocket().close();
          } catch ( IOException e ) {
          }
        }
      }
    }
    notifyAllObs();
  }

  @Override
  public void cleanUpOlderThan( long aTimestamp ) {
  }

  @Override
  public List< SocketProxy > getCheckedInPool() {
    return Collections.unmodifiableList( new ArrayList< SocketProxy >() );
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

}
