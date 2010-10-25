/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Date;

import org.apache.log4j.Logger;

public class SocketProxy {
  private static Logger LOGGER = Logger.getLogger(SocketProxy.class);

  private static boolean isTraceEnabled = false;

  private final SocketAddress myAddress;
  private Socket mySocket = null;
  private Date myConnectTime = null;
  private Exception myStackTrace = null;
  
  public SocketProxy(Socket aSocket){
    mySocket = aSocket;
    myAddress = aSocket.getRemoteSocketAddress();
    updateStackTrace();
  }

  public SocketProxy(SocketAddress anAddress){
    myAddress = anAddress;
    updateStackTrace();
  }

  public synchronized void connect() throws IOException{
    if(mySocket != null) return;

    Socket theSocket = new Socket();
    theSocket.connect( myAddress );
    myConnectTime = new Date();
    updateStackTrace();
    
    mySocket = theSocket;
  }



  public InputStream getInputStream() throws IOException {
    if(mySocket == null) connect();
    return mySocket.getInputStream();
  }

  public OutputStream getOutputStream() throws IOException {
    if(mySocket == null) connect();
    return mySocket.getOutputStream();
  }

  public boolean isBound() {
    if(mySocket == null) return false;
    return mySocket.isBound();
  }

  public boolean isClosed() {
    if(mySocket == null) return true;
    return mySocket.isClosed();
  }

  public boolean isConnected(){
    if(mySocket == null) return false;
    return mySocket.isConnected();
  }
  
  public boolean isInputShutdown() {
    if(mySocket == null) return true;
    return mySocket.isInputShutdown();
  }
  
  public boolean isOutputShutdown() {
    if(mySocket == null) return true;
    return mySocket.isOutputShutdown();
  }
  
  public SocketAddress getLocalSocketAddress() {
    if(mySocket == null) return null;
    return mySocket.getLocalSocketAddress();
  }
  
  public SocketAddress getRemoteSocketAddress() {
    if(mySocket == null) return null;
    return mySocket.getRemoteSocketAddress();
  }
  
  public InetAddress getLocalAddress() {
    if(mySocket == null) return null;
    return mySocket.getLocalAddress();
  }


  public synchronized void close(){
    if(mySocket != null){
      try {
        mySocket.close();
      } catch ( IOException e ) {
        LOGGER.error( "Unable to close socket", e);
      }
    }
    mySocket = null;
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
