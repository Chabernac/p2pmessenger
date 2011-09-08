/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.userinfo;

import chabernac.jna.UserPresenceDetector;
import chabernac.jna.UserState;
import chabernac.jna.iUserPresenceListener;
import chabernac.protocol.userinfo.UserInfo.Status;

public class AutoUserInfoStatusDector {
  private final UserInfo myUserInfo;
  private final UserPresenceDetector myDetector = new UserPresenceDetector( 60, 60 );
  private Status myLastStatus;
  
  public AutoUserInfoStatusDector( UserInfo aUserInfo ) {
    super();
    myUserInfo = aUserInfo;
    myLastStatus = aUserInfo.getStatus();
    myDetector.addListener( new UserPresenceListener() );
  }

  public void start(){
  myDetector.start();
  }
  
  public void stop(){
    myDetector.stop();
  }
  
  private class UserPresenceListener implements iUserPresenceListener {

    @Override
    public void userStateChanged( UserState aState ) {
      if(aState == UserState.AWAY){
        myLastStatus = myUserInfo.getStatus();
        myUserInfo.setStatus( Status.AWAY );
      } else {
        if(myLastStatus == Status.AWAY){
          myUserInfo.setStatus( Status.ONLINE );
        } else {
          myUserInfo.setStatus( myLastStatus );
        }
      }
    }
  }
}
