/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import chabernac.tools.NetTools;

public class Peer {
  private long myPeerId;
  private List<String> myHost = null;
  private int myPort;
  
  public Peer (){}
  
  public Peer(long aPeerId, String aHost, int aPort){
    myPeerId = aPeerId;
    if(myHost == null){
      myHost = new ArrayList<String>();
    }
    myHost.add(aHost);
    myPort = aPort;
  }
  
  public Peer ( long anPeerId ) {
    super();
    myPeerId = anPeerId;
  }
  
  public void detecteLocalInterfaces() throws SocketException{
    myHost = NetTools.getLocalExposedIpAddresses();
  }
  
  public String getHost() {
    return myHost;
  }
  public void setHost( String anHost ) {
    myHost = anHost;
  }
  public int getPort() {
    return myPort;
  }
  public void setPort( int anPort ) {
    myPort = anPort;
  }
  
  public long getPeerId() {
    return myPeerId;
  }

  public void setPeerId( long anPeerId ) {
    myPeerId = anPeerId;
  }

  public boolean equals(Object anObject){
    if(!(anObject instanceof Peer)) return false;
    Peer thePeer = (Peer)anObject;
    
    return myPeerId == thePeer.getPeerId();
  }
  
  public int hashCode(){
    return (int)myPeerId;
  }
  /**
   * create socket
   * send the message
   * close the sockket
   * 
   * @param aMessage
   * @throws IOException 
   * @throws UnknownHostException 
   */
  public String send(String aMessage) throws UnknownHostException, IOException{
    Socket theSocket = new Socket(myHost, myPort);
    PrintWriter theWriter = null;
    BufferedReader theReader = null;
    try{
      theWriter = new PrintWriter(new OutputStreamWriter(theSocket.getOutputStream()));
      theReader = new BufferedReader(new InputStreamReader(theSocket.getInputStream()));
      theWriter.println(aMessage);
      theWriter.flush();
      return theReader.readLine();
    }finally{
      if(theWriter != null){
        theWriter.close();
      }
      if(theReader != null){
        theReader.close();
      }
    }
  }
}
