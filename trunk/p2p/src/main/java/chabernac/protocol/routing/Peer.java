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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import chabernac.io.SocketPoolFactory;
import chabernac.io.SocketProxy;
import chabernac.io.iSocketPool;
import chabernac.tools.NetTools;

public class Peer implements Serializable {
  private static Logger LOGGER = Logger.getLogger(Peer.class);

  private static final long serialVersionUID = 7852961137229337616L;
  private String myPeerId;
  private List<String> myHost = null;
  private int myPort;
  private String myProtocolsString = null;
  private String myChannel = "default";

  public Peer (){}

  public Peer(String aPeerId, int aPort) throws NoAvailableNetworkAdapterException{
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

  public void detectLocalInterfaces() throws NoAvailableNetworkAdapterException{
    try {
      myHost = NetTools.getLocalExposedIpAddresses();
    } catch ( SocketException e ) {
      throw new NoAvailableNetworkAdapterException("Could not detect local network adapter", e);
    }
    if(myHost.size() == 0){
      throw new NoAvailableNetworkAdapterException("There is no available network adapter on this system");
    }
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

  public String getChannel() {
    return myChannel;
  }

  public void setChannel(String anChannel) {
    myChannel = anChannel;
  }
  
  public boolean isOnSameChannel(Peer anOtherPeer){
    return getChannel().equalsIgnoreCase(anOtherPeer.getChannel());
  }

  public boolean equals(Object anObject){
    if(!(anObject instanceof Peer)) return false;
    Peer thePeer = (Peer)anObject;

    return myPeerId.equals(thePeer.getPeerId());
  }

  public int hashCode(){
    return myPeerId.hashCode();
  }
  
  public String send(String aMessage) throws UnknownHostException, IOException{
    return send(aMessage, 5);
  }

  public String send(String aMessage, int aTimeoutInSeconds) throws UnknownHostException, IOException{
    Socket theSocket = createSocket( myPort );
    
    if(theSocket == null) throw new IOException("Could not open socket to peer: " + getPeerId() + " " + getHosts() + ":" + getPort());
    
    ScheduledExecutorService theService = Executors.newScheduledThreadPool( 1 );

    BufferedReader theReader = null;
    PrintWriter theWriter = null;
    try{
      theService.schedule( new SocketCloser(theSocket), aTimeoutInSeconds, TimeUnit.SECONDS );
      theWriter = new PrintWriter(new OutputStreamWriter(theSocket.getOutputStream()));
      theReader = new BufferedReader(new InputStreamReader(theSocket.getInputStream()));
      theWriter.println(aMessage);
      theWriter.flush();
      String theReturnMessage = theReader.readLine();
      
      return theReturnMessage;
    }finally{
      theService.shutdownNow();
      SocketPoolFactory.getSocketPool().checkIn( theSocket );
    }
  }



  /**
   * this method creates a socket by using the socket pool
   * you must call check in or close on the connection pool after you've used this socket!!
   * @param aPort
   * @return
   */
  public synchronized Socket createSocket(int aPort){
    iSocketPool<SocketProxy> theSocketPool = SocketPoolFactory.getSocketPool();

    for(Iterator< String > i = new ArrayList<String>(myHost).iterator(); i.hasNext();){
      String theHost = i.next();
      try{
        Socket theSocket = theSocketPool.checkOut(new InetSocketAddress(theHost, aPort));
        myHost.remove( theHost );
        myHost.add( 0, theHost);
        return theSocket;
      }catch(Exception e){
//        LOGGER.error("Could not open connection to peer: " + myHost + ":" + myPort, e);
      }
    }
    return null;
  }
  
  public String toString(){
    StringBuilder theBuilder = new StringBuilder();
    theBuilder.append( getPeerId() );
    theBuilder.append("@");
    theBuilder.append(getChannel());
    theBuilder.append( " (" );
    if(getHosts() != null && getHosts().size() > 0){
      for(Iterator< String > i = getHosts().iterator();i.hasNext();){
        String theHost = i.next();
        theBuilder.append( theHost );
        if(i.hasNext()) theBuilder.append( "," );
      }
    }
    theBuilder.append( ":" );
    theBuilder.append( getPort());
    theBuilder.append(")");
    return theBuilder.toString();
  }
  
  public boolean isSameHostAndPort(Peer aPeer){
    List<String> theHosts = aPeer.getHosts();
    boolean isSameHost = false;
    for(String theHost : theHosts){
      isSameHost |= getHosts().contains( theHost );
    }
    if(!isSameHost) return false;
    return getPort() == aPeer.getPort(); 
  }
  
  private class SocketCloser implements Runnable{
    private final Socket mySocket;

    public SocketCloser ( Socket anSocket ) {
      super();
      mySocket = anSocket;
    }
    
    public void run(){
      SocketPoolFactory.getSocketPool().close(  mySocket );
    }
  }
}
