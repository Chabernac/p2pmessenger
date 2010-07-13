/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.net.Socket;
import java.util.Map;

public interface iPeerSocketListener {
  public void peerSocketsChanged( Map<String, Socket> aSocketMap);
} 
