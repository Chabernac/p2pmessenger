/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui.event;

import java.util.Map;

import chabernac.events.Event;
import chabernac.protocol.userinfo.UserInfo;

public class UserInfoChangeEvent extends Event {
  private static final long serialVersionUID = 696058730799766288L;
  
  private final UserInfo aUserInfo;
  private final Map< String, UserInfo > aFullUserInfoList;
  
  public UserInfoChangeEvent ( UserInfo anUserInfo , Map< String, UserInfo > anFullUserInfoList ) {
    super();
    aUserInfo = anUserInfo;
    aFullUserInfoList = anFullUserInfoList;
  }

  public UserInfo getAUserInfo() {
    return aUserInfo;
  }

  public Map< String, UserInfo > getAFullUserInfoList() {
    return aFullUserInfoList;
  }
}
