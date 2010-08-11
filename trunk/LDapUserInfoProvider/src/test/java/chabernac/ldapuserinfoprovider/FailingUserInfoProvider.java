/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.ldapuserinfoprovider;

import chabernac.protocol.userinfo.UserInfo;
import chabernac.protocol.userinfo.UserInfoException;
import chabernac.protocol.userinfo.iUserInfoProvider;

public class FailingUserInfoProvider implements iUserInfoProvider {
  
  private boolean isFail = false;

  public void fillUserInfo( UserInfo aUserInfo ) throws UserInfoException {
    if(isFail) throw new UserInfoException("Could not load user info");
  }
  
  public void setFail(boolean isFail){
    this.isFail = isFail;
  }
}
