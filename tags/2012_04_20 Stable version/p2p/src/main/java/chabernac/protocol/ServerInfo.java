/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol;

import chabernac.io.iSocketSender;

public class ServerInfo {
  public static enum Type{WEB, SOCKET, STREAM_SPLITTING_SOCKET};
  
  private final Type myServerType;
  
  public ServerInfo(Type aServerType){
    myServerType = aServerType;
  }
  
  private int myServerPort;
  private String myServerURL;
  private iSocketSender mySocketSender;

  public int getServerPort() {
    return myServerPort;
  }

  public void setServerPort( int anServerPort ) {
    myServerPort = anServerPort;
  }

  public Type getServerType() {
    return myServerType;
  }

  public String getServerURL() {
    return myServerURL;
  }

  public void setServerURL( String anServerURL ) {
    myServerURL = anServerURL;
  }

  public iSocketSender getSocketSender() {
    return mySocketSender;
  }

  public void setSocketSender( iSocketSender aSocketSender ) {
    mySocketSender = aSocketSender;
  }
}
