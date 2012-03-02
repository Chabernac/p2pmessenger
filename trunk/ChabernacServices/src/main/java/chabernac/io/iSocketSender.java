/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.io.IOException;
import java.net.Socket;

public interface iSocketSender {
  
  /**
   * Send a message to a host with the given id host name and port
   * 
   * anId: the id of the receiving host 
   */
  public String send(String anId, String aHost, int aPort, String aMessage) throws IOException;
  public Socket getSocket(String anId);
}
