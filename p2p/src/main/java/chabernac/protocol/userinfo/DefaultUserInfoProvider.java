/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.userinfo;

import chabernac.protocol.userinfo.UserInfo.Status;

public class DefaultUserInfoProvider implements iUserInfoProvider {
  private boolean isFirstTime = true;
  private String myEmail;
  private String myId;
  private String myName;
  private String myTelNr;
  private Status myStatus = Status.ONLINE;
  
  public DefaultUserInfoProvider(){
    myId = System.getProperty( "user.name" );
  }

  @Override
  public void fillUserInfo( UserInfo aUserInfo ) throws UserInfoException {
    aUserInfo.setId( myId );
    aUserInfo.setEMail( myEmail );
    aUserInfo.setName( myName );
    aUserInfo.setTelNr( myTelNr );
    aUserInfo.setStatus( myStatus );
  }


  public void setEMail( String anEmail) {
    myEmail = anEmail;
  }


  public void setId( String anId) {
    myId = anId;
  }


  public void setName( String anName ) {
    myName = anName;
  }


  public void setTelNr( String anTelNr ) {
    myTelNr = anTelNr;
  }

  public void setStatus( Status anStatus ) {
    myStatus = anStatus;
  }
  
  
}
