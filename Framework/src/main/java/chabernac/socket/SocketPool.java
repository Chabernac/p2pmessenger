/*
 * Copyright (c) 1998 Anhyp, NV. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Anhyp.
 *
 */

package chabernac.socket;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;

import chabernac.log.Logger;
import chabernac.server.Channel;

/**
 *
 *
 * @version v1.0.0      Dec 13, 2005
 *<pre><u><i>Version History</u></i>
 *
 * v1.0.0 Dec 13, 2005 - initial release       - Guy Chauliac
 *
 *</pre>
 *
 * @author <a href="mailto:guy.chauliac@axa.be"> Guy Chauliac </a>
 */
public class SocketPool {
  
  private static Hashtable myPools = new Hashtable();
  
  private int poolSize = 1;
  private ArrayList myFreeSockets;
  private ArrayList myUsedSockets;
  private int current = 0;
  private String host = null;
  private int port;
  
  public SocketPool(String host, int port, int poolSize) {
    myFreeSockets = new ArrayList(poolSize);
    myUsedSockets = new ArrayList(poolSize);
    this.host = host;
    this.port = port;
  }
  
  public static SocketPool getPool(String aHost, int aPort) throws PoolException{
    String theKey = aHost + "_" + aPort;
    if(!myPools.containsKey(theKey)) throw new PoolException("Pool is not created yet, use SocketPool.createPool to create pool first");
    return (SocketPool)myPools.get(theKey);
  }
  
  public static void createPool(String aHost, int aPort, int aPoolSize){
    String theKey = aHost + "_" + aPort;
    if(myPools.containsKey(theKey)) return;
    myPools.put(theKey, new SocketPool(aHost, aPort, aPoolSize));
  }
  
  public synchronized Socket getSocket() throws UnknownHostException, IOException{
    if(myFreeSockets.isEmpty() && myUsedSockets.size() < poolSize){
      myFreeSockets.add(new Socket(host, port));
    }
    while(myFreeSockets.isEmpty()){
      try{
        wait();
      }catch(InterruptedException e){
        Logger.log(this,"Could not wait", e);
      }
    }
    Socket theSocket = (Socket)myFreeSockets.get(0);
    myFreeSockets.remove(0);
    
    if(!theSocket.isConnected()){
      theSocket = new Socket(host, port);
    }
    myUsedSockets.add(theSocket);
    
    return theSocket;
  }
  
  public synchronized void freeSocket(Socket aSocket){
    if(myUsedSockets.contains(aSocket)){
      myUsedSockets.remove(aSocket);
      myFreeSockets.add(aSocket);
      notify();
    }
  }
  
  public synchronized boolean hasFreeSocket(){
    return !myFreeSockets.isEmpty();
  }
  
  public void freeAllSockets(){
    for(int i=0;i<myUsedSockets.size();i++){
      Socket theSocket = (Socket)myUsedSockets.get(i);
      try{
        theSocket.close();
        myUsedSockets.remove(theSocket);
      }catch(IOException e){
        Logger.log(this,"Could not close socket", e);
      }
    }
  }
  
  public static void main(String args[]){
    Logger.setDebug(true);
    SocketPool.createPool("localhost", 789, 2);
    try{
      SocketPool thePool = SocketPool.getPool("localhost", 789);
      
      for(int i=0;i<10;i++){
        Socket theSocket = thePool.getSocket();
        Channel theChannel = new Channel(theSocket.getInputStream(), theSocket.getOutputStream());
        theChannel.write("hallo" + System.currentTimeMillis());
        if(i==0) Logger.log(theSocket, theChannel.read());
        Logger.log(thePool, theChannel.read());
        thePool.freeSocket(theSocket);
      }
      
      
      Socket theSocket = thePool.getSocket();
      Channel theChannel = new Channel(theSocket.getInputStream(), theSocket.getOutputStream());
      theChannel.write("quit");
      Logger.log(thePool, theChannel.read());
      thePool.freeSocket(theSocket);
      
    }catch(PoolException e){
      e.printStackTrace();
    } catch (UnknownHostException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
  }

  
}
