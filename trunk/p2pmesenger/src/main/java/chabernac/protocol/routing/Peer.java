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
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import chabernac.tools.NetTools;

public class Peer implements Serializable {
  private static final long serialVersionUID = 7852961137229337616L;
  private String myPeerId;
  private List<String> myHost = null;
  private int myPort;
  private String myProtocolsString = null;

  public Peer (){}
  
  public Peer(String aPeerId, int aPort) throws SocketException{
    myPeerId = aPeerId;
    myPort = aPort;
    detectLocalInterfaces();
  }

  public Peer(String aPeerId, List<String> aHosts, int aPort){
    myPeerId = aPeerId;
    myHost = aHosts;
    myPort = aPort;
  }
  
  public Peer(String aPeerId, String aHost, int aPort){
    myPeerId = aPeerId;
    if(myHost == null){
      myHost = new ArrayList<String>();
    }
    myHost.add(aHost);
    myPort = aPort;
  }

  public Peer (String anPeerId ) {
    super();
    myPeerId = anPeerId;
  }

  public void detectLocalInterfaces() throws SocketException{
    myHost = NetTools.getLocalExposedIpAddresses();
  }

  public List<String> getHosts() {
    return myHost;
  }
  public void setHosts( List<String> anHost ) {
    myHost = anHost;
  }
  public int getPort() {
    return myPort;
  }
  public void setPort( int anPort ) {
    myPort = anPort;
  }

  public String getPeerId() {
    return myPeerId;
  }

  public void setPeerId( String anPeerId ) {
    myPeerId = anPeerId;
  }

  public String getProtocolsString() {
    return myProtocolsString;
  }

  public void setProtocolsString( String anProtocolsString ) {
    myProtocolsString = anProtocolsString;
  }

  public boolean equals(Object anObject){
    if(!(anObject instanceof Peer)) return false;
    Peer thePeer = (Peer)anObject;

    return myPeerId.equals(thePeer.getPeerId());
  }

  public int hashCode(){
    return myPeerId.hashCode();
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
    boolean send = false;
    for(Iterator< String > i = myHost.iterator(); i.hasNext() && !send;){
      String theHost = i.next();
      Socket theSocket = new Socket(theHost, myPort);
//      theSocket.setSoTimeout( 1000 );
      PrintWriter theWriter = null;
      BufferedReader theReader = null;
      try{
        theWriter = new PrintWriter(new OutputStreamWriter(theSocket.getOutputStream()));
        theReader = new BufferedReader(new InputStreamReader(theSocket.getInputStream()));
        theWriter.println(aMessage);
        theWriter.flush();
        String theReturnMessage = theReader.readLine();
        
        send = true;
        //set this host at the top of the list if it is not already.  This is
        //to avoid a sending delay next time send() is called
        if(myHost.indexOf( theHost ) != 0){
          myHost.remove( theHost );
          myHost.add( 0, theHost );
        }
        theSocket.close();
        return theReturnMessage;
      }finally{
        if(theWriter != null){
          theWriter.close();
        }
        if(theReader != null){
          theReader.close();
        }
      }
    }
    throw new UnknownHostException("No hosts for this peer");
  }
  
  public Socket createSocket(int aPort){
    for(Iterator< String > i = myHost.iterator(); i.hasNext();){
      try{
        Socket theSocket = new Socket(i.next(), aPort);
        return theSocket;
      }catch(Exception e){}
    }
    return null;
  }
}
