/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.userinfo;

import java.util.Map;

public interface iUserInfoListener {
  public void userInfoChanged(UserInfo aUserInfo, Map<String,  UserInfo > aFullUserInfoList);
}
