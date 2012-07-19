/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.io.IOException;
import java.net.Socket;

import javax.swing.JPanel;

public interface iSocketSender {
  
  /**
   * Send a message to a host with the given id host name and port
   * 
   * anId: the id of the receiving host 
   */
  
  public SocketSenderReply send(String aHost, int aPort, String amessage) throws IOException;
  public SocketSenderReply send(String anId, String aMessage) throws IOException;
  public String getRemoteId(String aHost, int aPort) throws IOException;
  public Socket getSocket(String anId);
  public boolean containsSocketForId(String anId);
  
  public JPanel getDebuggingPanel();
}
