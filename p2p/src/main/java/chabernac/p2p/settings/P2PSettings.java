/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2p.settings;

import chabernac.io.BasicSocketPool;
import chabernac.io.iSocketPool;
import chabernac.protocol.routing.PeerSender;
import chabernac.protocol.routing.iPeerSender;

public class P2PSettings {
  private iPeerSender myPeerSender = new PeerSender();
  private iSocketPool mySocketPool = new BasicSocketPool();
  
  
  public P2PSettings(){
    mySocketPool = new BasicSocketPool();
    ((BasicSocketPool)mySocketPool).setSocketReuse( false );
  }
  
  private static final class INSTANCE_HOLDER{
    private static final P2PSettings INSTANCE = new P2PSettings();
  }
  
  public static P2PSettings getInstance(){
    return INSTANCE_HOLDER.INSTANCE;
  }
  
  public iPeerSender getPeerSender() {
    return myPeerSender;
  }
  public void setPeerSender( iPeerSender aPeerSender ) {
    myPeerSender = aPeerSender;
  }
  public iSocketPool getSocketPool() {
    return mySocketPool;
  }
  public void setSocketPool( iSocketPool aSocketPool ) {
    mySocketPool = aSocketPool;
  }
  
  
}
