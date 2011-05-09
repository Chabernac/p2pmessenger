/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2p.settings;

import java.util.concurrent.TimeUnit;

import chabernac.io.BasicSocketPool;
import chabernac.io.SocketPoolCleanUpDecorator;
import chabernac.io.iSocketPool;
import chabernac.protocol.routing.PeerSender;
import chabernac.protocol.routing.iPeerSender;

public class P2PSettings {
  private iSocketPool mySocketPool = new BasicSocketPool();
  
  
  public P2PSettings(){
    BasicSocketPool theSocketPool = new BasicSocketPool();
    theSocketPool.setSocketReuse( true );
    mySocketPool = new SocketPoolCleanUpDecorator( theSocketPool, 30, TimeUnit.SECONDS);
  }
  
  private static final class INSTANCE_HOLDER{
    private static final P2PSettings INSTANCE = new P2PSettings();
  }
  
  public static P2PSettings getInstance(){
    return INSTANCE_HOLDER.INSTANCE;
  }
  
  public iSocketPool getSocketPool() {
    return mySocketPool;
  }
  public void setSocketPool( iSocketPool aSocketPool ) {
    mySocketPool = aSocketPool;
  }
}
