/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PeerSocketFactory {
  private Map<String, Socket> mySockets = Collections.synchronizedMap( new HashMap< String, Socket> ());
//  private static Logger LOGGER = Logger.getLogger(PeerSocketFactory.class);
  private ScheduledExecutorService myMonitorService = Executors.newScheduledThreadPool( 1 );

  private static class InstanceHolder{
    private static PeerSocketFactory INSTANCE = new PeerSocketFactory();
  }
  
  private List< iPeerSocketListener > myListeners = new ArrayList< iPeerSocketListener >();
  
  private PeerSocketFactory(){
    myMonitorService.scheduleWithFixedDelay( new CleanUp(), 30, 30, TimeUnit.SECONDS );
  }

  public static PeerSocketFactory getInstance(){
    return InstanceHolder.INSTANCE;
  }
  
  public void addListener(iPeerSocketListener aListener){
    myListeners.add(aListener);
  }
  
  public void removeListener(iPeerSocketListener aListener){
    myListeners.remove( aListener );
  }
  
  public void notifyListeners(){
    for(iPeerSocketListener theListener : myListeners){
      theListener.peerSocketsChanged( mySockets );
    }
  }

  public Socket getSocketForPeer(Peer aPeer) throws IOException{
//    long t1 = System.currentTimeMillis();

    try{
      if(aPeer.getPeerId() == null || aPeer.getPeerId().equals( "" )){
        Socket theSocket = aPeer.createSocket( aPeer.getPort() );
        if(theSocket == null){
          throw new IOException("Could not create socket witht peer '" +  aPeer.getPeerId() + "' on '" + aPeer.getHosts() + ": " + aPeer.getPort() + "'");
        }
        return theSocket;
      }

      synchronized ( aPeer.getPeerId().intern() ) {
        if(!mySockets.containsKey( aPeer.getPeerId() ) || !mySockets.get( aPeer.getPeerId() ).isConnected()){
          Socket theSocket = aPeer.createSocket( aPeer.getPort() );
          if(theSocket == null){
            throw new IOException("Could not create socket with peer '" +  aPeer.getPeerId() + "' on '" + aPeer.getHosts() + ": " + aPeer.getPort() + "'");
          }
          mySockets.put(aPeer.getPeerId(), theSocket);
          notifyListeners();
        }

        return mySockets.get( aPeer.getPeerId() );
      }
    }finally{
//      LOGGER.debug("Creating socket to peer '" + aPeer.getPeerId() + "' on " + aPeer.getHosts() + ":" + aPeer.getPort() + "' took " + (System.currentTimeMillis() - t1) + " ms");
    }
  }

  public void clear(){
    for(Socket theSocket : mySockets.values()){
      try {
        //synchronize on the socket so that it can not be closed while the sockete is being used
        synchronized ( theSocket ) {
          theSocket.close();
        }
      } catch ( IOException e ) {
      }
    }
    mySockets.clear();
    notifyListeners();
  }
  
  protected void finalize(){
    clear();
  }
  
  public TreeMap< String, Socket > getSockets(){
    return new TreeMap< String, Socket >(mySockets);
  }
  
  private class CleanUp implements Runnable {

    @Override
    public void run() {
      clear();
    }
  }

}
