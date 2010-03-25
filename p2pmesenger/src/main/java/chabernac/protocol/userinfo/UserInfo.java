/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.userinfo;

public class UserInfo {
  private String myId = null;
  private String myName = null;
  private String myEMail = null;
  private String myTelNr = null;
  private String myLocation = null;
  
  public String getId() {
    return myId;
  }
  public void setId( String anId ) {
    myId = anId;
  }
  public String getName() {
    return myName;
  }
  public void setName( String anName ) {
    myName = anName;
  }
  public String getEMail() {
    return myEMail;
  }
  public void setEMail( String anMail ) {
    myEMail = anMail;
  }
  public String getTelNr() {
    return myTelNr;
  }
  public void setTelNr( String anTelNr ) {
    myTelNr = anTelNr;
  }
  public String getLocation() {
    return myLocation;
  }
  public void setLocation( String anLocation ) {
    myLocation = anLocation;
  }
}
