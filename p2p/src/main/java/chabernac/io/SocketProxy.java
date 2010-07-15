/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Date;

public class SocketProxy {
  private final SocketAddress myAddress;
  private Socket mySocket = null;
  private Date myConnectTime = null;
  
  public SocketProxy(SocketAddress anAddress){
    myAddress = anAddress;
  }
  
  public synchronized Socket connect() throws IOException{
    if(mySocket == null){
      mySocket = new Socket();
      myConnectTime = new Date();
      mySocket.connect( myAddress );
    }
    return mySocket;
  }
  
  public boolean isConnected(){
    return mySocket != null && mySocket.isConnected();
  }
  
  public Socket getSocket(){
    return mySocket;
  }
  
  public SocketAddress getSocketAddress(){
    return myAddress;
  }

  public Date getConnectTime() {
    return myConnectTime;
  }

  public void setConnectTime( Date anConnectTime ) {
    myConnectTime = anConnectTime;
  }
}
