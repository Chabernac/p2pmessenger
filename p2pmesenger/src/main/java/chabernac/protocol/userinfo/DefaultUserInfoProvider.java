/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.userinfo;

public class DefaultUserInfoProvider implements iUserInfoProvider {
  private UserInfo myUserInfo = null;
  
  public DefaultUserInfoProvider(){
    myUserInfo = new UserInfo();
    myUserInfo.setId( System.getProperty( "user.name" ) );
  }

  @Override
  public UserInfo getUserInfo() {
    return myUserInfo;
  }

}
