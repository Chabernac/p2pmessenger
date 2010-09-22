/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import chabernac.io.SocketPoolFactory;
import chabernac.io.SocketProxy;
import chabernac.io.iSocketPool;
import chabernac.tools.NetTools;

public class SocketPeer extends AbstractPeer implements Serializable {
  private static final long serialVersionUID = 7852961137229337616L;
  private List<String> myHost = null;
  private int myPort;
  private String myChannel = "default";

  public SocketPeer (){
    super(null);
  }

  public SocketPeer(String aPeerId, int aPort) throws NoAvailableNetworkAdapterException{
    super(aPeerId);
    myPort = aPort;
    detectLocalInterfaces();
  }

  public SocketPeer(String aPeerId, List<String> aHosts, int aPort){
    super(aPeerId);
    myHost = aHosts;
    myPort = aPort;
  }

  public SocketPeer(String aPeerId, String aHost, int aPort){
    super(aPeerId);
    if(myHost == null){
      myHost = new ArrayList<String>();
    }
    myHost.add(aHost);
    myPort = aPort;
  }

  public SocketPeer (String anPeerId ) {
    super(anPeerId);
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

  public String getChannel() {
    return myChannel;
  }

  public void setChannel(String anChannel) {
    myChannel = anChannel;
  }
  
  public String send(String aMessage) throws IOException{
    return send(aMessage, 5);
  }

  public String send(String aMessage, int aTimeoutInSeconds) throws IOException{
    if(PeerSenderHolder.getPeerSender() == null) throw new IOException("Could not send message to peer '" + getPeerId() + " because no message sender was defined");
    
    return PeerSenderHolder.getPeerSender().send(aMessage, this, aTimeoutInSeconds);
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
  
  public boolean isSameEndPointAs(AbstractPeer aPeer){
    if(!(aPeer instanceof SocketPeer)) return false;
    
    SocketPeer thePeer = (SocketPeer)aPeer;
    List<String> theHosts = thePeer.getHosts();
    boolean isSameHost = false;
    for(String theHost : theHosts){
      isSameHost |= getHosts().contains( theHost );
    }
    if(!isSameHost) return false;
    return getPort() == thePeer.getPort(); 
  }

  @Override
  public boolean isValidEndPoint() {
    if(myHost == null) return false;
    if(myHost.size() == 0) return false;
    if(myPort <= 0) return false;
    return true;
  }

  @Override
  public String getEndPointRepresentation() {
   return myHost + ":" + myPort;
  }
}
