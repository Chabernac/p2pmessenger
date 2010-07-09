/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.testingutils;

import chabernac.protocol.userinfo.UserInfo;
import chabernac.protocol.userinfo.iUserInfoProvider;

public class UserInfoProvider implements iUserInfoProvider {
  private String myName;
  private String myMail;
  
  public UserInfoProvider(String aName, String aMail){
    myName = aName;
    myMail = aMail;
  }

  @Override
  public UserInfo getUserInfo() {
   UserInfo theUserInfo = new UserInfo();
   theUserInfo.setEMail( myMail );
   theUserInfo.setName( myName );
   return theUserInfo;
  }

}
