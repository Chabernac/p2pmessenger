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

import org.apache.log4j.Logger;

import chabernac.tools.NetTools;

public class Peer implements Serializable {
  private static Logger LOGGER = Logger.getLogger(Peer.class);

  private static final long serialVersionUID = 7852961137229337616L;
  private String myPeerId;
  private List<String> myHost = null;
  private int myPort;
  private String myProtocolsString = null;

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
  public synchronized String send(String aMessage) throws UnknownHostException, IOException{
    Socket theSocket = PeerSocketFactory.getInstance().getSocketForPeer( this );

    BufferedReader theReader = null;
    PrintWriter theWriter = null;
    long t1 = System.currentTimeMillis();
    try{
      theWriter = new PrintWriter(new OutputStreamWriter(theSocket.getOutputStream()));
      theReader = new BufferedReader(new InputStreamReader(theSocket.getInputStream()));
      theWriter.println(aMessage);
      theWriter.flush();
      String theReturnMessage = theReader.readLine();

      return theReturnMessage;
    }finally{
      System.out.println("Sending message took " + (System.currentTimeMillis() - t1) + " ms");
//      if(theWriter != null){
//        theWriter.close();
//      }
//      if(theReader != null){
//        theReader.close();
//      }
    }
  }

//  public Socket createSocket(int aPort){
//    for(Iterator< String > i = new ArrayList<String>(myHost).iterator(); i.hasNext();){
//      String theHost = i.next();
//      try{
//        Socket theSocket = new Socket();
////        theSocket.setReuseAddress( true );
//        theSocket.setSoTimeout( 8000 );
//        theSocket.connect( new InetSocketAddress(theHost, aPort) );
//        //if we succeed in connecting to this host at this address then pust the host name at the top
//        //so that the next socket creation will go faster
//        myHost.remove( theHost );
//        myHost.add( 0, theHost);
//        return theSocket;
//      }catch(Exception e){
////        LOGGER.error( "Error occured while connecting to '" + theHost + "':'" + aPort + "'",e );
//      }
//    }
//    return null;
//  }

  public Socket createSocket(int aPort){
    for(Iterator< String > i = new ArrayList<String>(myHost).iterator(); i.hasNext();){
      String theHost = i.next();
      try{
        Socket theSocket = new Socket(theHost, aPort);
        myHost.remove( theHost );
        myHost.add( 0, theHost);
        return theSocket;
      }catch(Exception e){}
    }
    return null;
  }
}
