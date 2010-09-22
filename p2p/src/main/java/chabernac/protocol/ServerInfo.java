/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol;

public class ServerInfo {
  public static enum Type{WEB, SOCKET};
  
  private final Type myServerType;
  
  public ServerInfo(Type aServerType){
    myServerType = aServerType;
  }
  
  private int myServerPort;
  private String myServerURL;

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
}
