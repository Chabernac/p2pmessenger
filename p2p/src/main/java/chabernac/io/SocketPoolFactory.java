/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;


public class SocketPoolFactory {
  private static iSocketPool<SocketProxy> INSTANCE = new BasicSocketPool();
  
  public static iSocketPool<SocketProxy> getSocketPool(){
    return INSTANCE;
  }
}
