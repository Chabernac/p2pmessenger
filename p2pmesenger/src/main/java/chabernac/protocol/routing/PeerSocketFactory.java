/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class PeerSocketFactory {
  private Map<String, Socket> mySockets = new HashMap< String, Socket> ();

  private static class InstanceHolder{
    private static PeerSocketFactory INSTANCE = new PeerSocketFactory();
  }

  public static PeerSocketFactory getInstance(){
    return InstanceHolder.INSTANCE;
  }

  public Socket getSocketForPeer(Peer aPeer) throws IOException{
//    Socket theSocket = aPeer.createSocket( aPeer.getPort() );
//    if(theSocket == null){
//      throw new IOException("Could not create socket witht peer '" +  aPeer.getPeerId() + "' on '" + aPeer.getHosts() + ": " + aPeer.getPort() + "'");
//    }
//    return theSocket;
    
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
      }
      
      return mySockets.get( aPeer.getPeerId() );
    }
  }
  
  public void clear(){
    mySockets.clear();
  }

}
