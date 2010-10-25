/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;

public class SimpleSocketPool extends Observable implements iSocketPool{
  private List<SocketProxy> myCheckedOutPool = new ArrayList< SocketProxy >() ;
  private List<SocketProxy> myConnectingPool = new ArrayList< SocketProxy >() ;

  private Object LOCK = new Object();

  @Override
  public SocketProxy checkOut( SocketAddress anAddress ) throws IOException{
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

  @Override
  public void checkIn( SocketProxy aSocket ) {
    if(aSocket == null) return;
    aSocket.close();
    synchronized(LOCK){
      myCheckedOutPool.remove( aSocket );
      notifyAllObs();
    }
  }

  @Override
  public void close( SocketProxy aSocket ) {
    //close and check in are the same in the simple pool
    checkIn( aSocket );
  }

  @Override
  public void cleanUp() {
    synchronized (LOCK) {
      for(SocketProxy theSocket : myCheckedOutPool){
        theSocket.close();
      }
      for(SocketProxy theSocket : myConnectingPool){
        theSocket.close();
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
