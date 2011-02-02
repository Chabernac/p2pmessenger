/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.userinfo;

public class EmptyUserInfoProvider implements iUserInfoProvider {

  @Override
  public void fillUserInfo( UserInfo aUserInfo ) throws UserInfoException {
    aUserInfo.clear();
  }

}
