/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SocketProxy {
  private static boolean isTraceEnabled = false;

  private final SocketAddress myAddress;
  private Socket mySocket = null;
  private Date myConnectTime = null;
  private Exception myStackTrace = null;

  public SocketProxy(SocketAddress anAddress){
    myAddress = anAddress;
    updateStackTrace();
  }

  public synchronized Socket connect() throws IOException{
    if(mySocket == null){
      mySocket = new Socket();
      myConnectTime = new Date();
      ScheduledExecutorService theService = Executors.newScheduledThreadPool( 1 );
      try{
        theService.schedule( new SocketInterrupter(mySocket), 5, TimeUnit.SECONDS );
        mySocket.connect( myAddress );
      }finally{
        theService.shutdownNow();
      }
    }
    updateStackTrace();
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

  private void updateStackTrace(){
    if(isTraceEnabled){
      myStackTrace = new Exception();
      myStackTrace.fillInStackTrace();
    }
  }

  public String getStackTrace() {
    if(myStackTrace != null){
      StringWriter theStringWriter= new StringWriter();
      PrintWriter theWriter = new PrintWriter(theStringWriter);
      myStackTrace.printStackTrace( theWriter );
      return theStringWriter.getBuffer().toString();
    }
    return "no stack trace";
  }

  public static boolean isTraceEnabled() {
    return isTraceEnabled;
  }

  public static void setTraceEnabled(boolean anIsTraceEnabled) {
    isTraceEnabled = anIsTraceEnabled;
  }

  private class SocketInterrupter implements Runnable{
    private final Socket mySocket;

    public SocketInterrupter ( Socket anSocket ) {
      super();
      mySocket = anSocket;
    }
    
    public void run(){
      try {
        mySocket.close();
      } catch ( IOException e ) {
      }
    }
  }
}
