/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.userinfo;

import chabernac.protocol.userinfo.UserInfo.Status;

public class DefaultUserInfoProvider implements iUserInfoProvider {
  private UserInfo myUserInfo = null;
  
  public DefaultUserInfoProvider(){
    myUserInfo = new UserInfo();
    myUserInfo.setProperty( UserInfo.Property.ID, System.getProperty( "user.name" ) );
    myUserInfo.setStatus( Status.ONLINE );
  }

  @Override
  public UserInfo getUserInfo() {
    return myUserInfo;
  }

}
