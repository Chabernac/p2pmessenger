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

public class BasicSocketPool extends Observable implements iSocketPool<SocketProxy>{
  private List<SocketProxy> myCheckedOutPool = new ArrayList< SocketProxy >() ;
  private List<SocketProxy> myConnectingPool = new ArrayList< SocketProxy >() ;
  private List<SocketProxy> myCheckedInPool = new ArrayList< SocketProxy >() ;

  private Object LOCK = new Object();

  @Override
  public Socket checkOut( SocketAddress anAddress ) throws IOException{
    synchronized(LOCK){
      SocketProxy theProxy = searchProxyForSocketAddressInPool( myCheckedInPool, anAddress);
      if(theProxy != null){
        Socket theSocket = theProxy.connect();
        if(!theSocket.isBound() || 
            theSocket.isClosed() ||
            !theSocket.isConnected() ||
            theSocket.isInputShutdown() ||
            theSocket.isOutputShutdown()){
            closeProxy( theProxy );
          } else {
            myCheckedInPool.remove( theProxy );
            myCheckedOutPool.add( theProxy );
            return theProxy.connect();
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

    private SocketProxy searchProxyForSocketAddressInPool(List<SocketProxy> aPool, SocketAddress aSocketAddress){
      for(SocketProxy theProxy : myCheckedInPool){
        if(theProxy.getSocketAddress().equals( aSocketAddress )){
          return theProxy;
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

      synchronized(LOCK){
        myCheckedOutPool.remove( theProxy );
        myCheckedInPool.add( theProxy );
        notifyAllObs();
      }
    }

    private void closeProxy(SocketProxy aProxy){ 
      try {
        if(aProxy.getSocket() != null){
          aProxy.getSocket().close();
        }
      } catch ( IOException e ) {
      } finally {
        synchronized(LOCK){
          myCheckedOutPool.remove( aProxy );
          myCheckedInPool.remove(aProxy);
          notifyAllObs();
        }
      }
    }

    @Override
    public void close( Socket aSocket ) {
      SocketProxy theProxy = searchProxyForSocketInPool( myCheckedOutPool, aSocket );
      if(theProxy == null) theProxy = searchProxyForSocketInPool( myCheckedInPool, aSocket );

      if(theProxy == null) {
        try {
          aSocket.close();
        } catch ( IOException e ) {
        }
        return;
      }

      closeProxy( theProxy );
    }
    
    private void clean(List<SocketProxy> aPool){
      synchronized (LOCK) {
        for(SocketProxy theSocket : aPool){
          if(theSocket.getSocket() != null){
            try {
              theSocket.getSocket().close();
            } catch ( IOException e ) {
            }
          }
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
      // TODO Auto-generated method stub

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

  }
