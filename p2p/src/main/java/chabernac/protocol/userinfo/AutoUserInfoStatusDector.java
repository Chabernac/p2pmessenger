/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.userinfo;

import chabernac.jni.UserPresenceDetector;
import chabernac.jni.UserState;
import chabernac.jni.iUserPresenceListener;
import chabernac.protocol.userinfo.UserInfo.Status;

public class AutoUserInfoStatusDector {
  private final UserInfo myUserInfo;
  private final UserPresenceDetector myDetector = new UserPresenceDetector( 30, 30 );
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
        myUserInfo.setStatus( myLastStatus );
      }
    }
  }
}
