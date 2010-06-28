/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.ldapuserinfoprovider;

import chabernac.protocol.userinfo.UserInfo;
import chabernac.protocol.userinfo.UserInfoException;
import chabernac.protocol.userinfo.iUserInfoProvider;

public class FailingUserInfoProvider implements iUserInfoProvider {
  
  private UserInfo myUserInfo = null;

  public UserInfo getUserInfo() throws UserInfoException {
    if(myUserInfo == null) throw new UserInfoException("Could not load user info");
    return myUserInfo;
  }
  
  public void setUserInfo(UserInfo aUserInfo){
    myUserInfo = aUserInfo;
  }

}
