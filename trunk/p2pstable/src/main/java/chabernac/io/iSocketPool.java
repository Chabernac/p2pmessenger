/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.List;
import java.util.Observer;

public interface iSocketPool{
  public SocketProxy checkOut(SocketAddress anAddress) throws IOException;
  public void checkIn(SocketProxy aSocket);
  public void close(SocketProxy aSocket);
  
  public void cleanUp();
  public void cleanUpOlderThan(long aTimestamp);
  
  public List<SocketProxy> getCheckedInPool();
  public List<SocketProxy> getCheckedOutPool();
  public List<SocketProxy> getConnectingPool();
  
  public void addObserver(Observer anObserver);
  public void deleteObserver(Observer anObserver);
}
