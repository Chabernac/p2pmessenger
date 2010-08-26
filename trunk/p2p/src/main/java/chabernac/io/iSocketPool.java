/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;

public interface iSocketPool <T>{
  public Socket checkOut(SocketAddress anAddress) throws IOException;
  public void checkIn(Socket aSocket);
  public void close(Socket aSocket);
  
  public void cleanUp();
  public void cleanUpOlderThan(long aTimestamp);
  
  public List<T> getCheckedInPool();
  public List<T> getCheckedOutPool();
  public List<T> getConnectingPool();
  
}
