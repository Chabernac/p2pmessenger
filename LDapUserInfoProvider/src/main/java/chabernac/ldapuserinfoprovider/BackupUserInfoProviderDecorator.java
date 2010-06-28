/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.ldapuserinfoprovider;

import org.apache.log4j.Logger;

import chabernac.preference.ApplicationPreferences;
import chabernac.protocol.userinfo.UserInfo;
import chabernac.protocol.userinfo.UserInfoException;
import chabernac.protocol.userinfo.iUserInfoProvider;

public class BackupUserInfoProviderDecorator implements iUserInfoProvider {
  private static Logger LOGGER = Logger.getLogger(BackupUserInfoProviderDecorator.class);
  
  private final iUserInfoProvider myDelegate;


  public BackupUserInfoProviderDecorator ( iUserInfoProvider anDelegate ) {
    super();
    myDelegate = anDelegate;
  }


  public UserInfo getUserInfo() throws UserInfoException {
    try {
      UserInfo theUserInfo = myDelegate.getUserInfo();
      saveBackup(theUserInfo);
      return theUserInfo;
    }catch(UserInfoException e){
      LOGGER.error( "Faild to retrieve user info with class '" + myDelegate.getClass() + "' trying to load backup", e );
      return loadBackup();
    }
  }


  private UserInfo loadBackup() {
    UserInfo theUserInfo = new UserInfo();
    ApplicationPreferences thePrefereces = ApplicationPreferences.getInstance();
    theUserInfo.setEMail( thePrefereces.getProperty( "user.email" ) );
    theUserInfo.setName( thePrefereces.getProperty( "user.name", System.getProperty( "user.name" ) ));
    theUserInfo.setId( thePrefereces.getProperty( "user.id", System.getProperty( "user.name" ) ));
    theUserInfo.setLocation( thePrefereces.getProperty( "user.location"));
    theUserInfo.setTelNr( thePrefereces.getProperty( "user.telnr"));
    return theUserInfo;
  }


  private void saveBackup(UserInfo aUserInfo) {
   ApplicationPreferences.getInstance().setProperty( "user.name",  aUserInfo.getName());
   ApplicationPreferences.getInstance().setProperty( "user.email",  aUserInfo.getEMail());
   ApplicationPreferences.getInstance().setProperty( "user.id",  aUserInfo.getId());
   ApplicationPreferences.getInstance().setProperty( "user.location",  aUserInfo.getLocation());
   ApplicationPreferences.getInstance().setProperty( "user.telnr",  aUserInfo.getTelNr());
  }

}
