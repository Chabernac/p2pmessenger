/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.userinfo;

import chabernac.protocol.userinfo.UserInfo.Status;

public class DefaultUserInfoProvider implements iUserInfoProvider {
  private boolean isFirstTime = true;
  

  @Override
  public void fillUserInfo( UserInfo aUserInfo ) throws UserInfoException {
    aUserInfo.setProperty( UserInfo.Property.ID, System.getProperty( "user.name" ) );
    if(isFirstTime){
      aUserInfo.setStatus( Status.ONLINE );
      isFirstTime = false;
    }
  }

}
