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
  private UserInfo myUserInfoBackup = new UserInfo();


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
    ApplicationPreferences thePrefereces = ApplicationPreferences.getInstance();
    if(thePrefereces.containsKey( "user.email" )) myUserInfoBackup.setEMail( thePrefereces.getProperty( "user.email" ) );
    myUserInfoBackup.setName( thePrefereces.getProperty( "user.name", System.getProperty( "user.name" ) ));
    myUserInfoBackup.setId( thePrefereces.getProperty( "user.id", System.getProperty( "user.name" ) ));
    if(thePrefereces.containsKey( "user.location" )) myUserInfoBackup.setLocation( thePrefereces.getProperty( "user.location"));
    if(thePrefereces.containsKey( "user.telnr" )) myUserInfoBackup.setTelNr( thePrefereces.getProperty( "user.telnr"));
    return myUserInfoBackup;
  }


  private void saveBackup(UserInfo aUserInfo) {
   if(aUserInfo.getName() != null) ApplicationPreferences.getInstance().setProperty( "user.name",  aUserInfo.getName());
   if(aUserInfo.getEMail() != null) ApplicationPreferences.getInstance().setProperty( "user.email",  aUserInfo.getEMail());
   if(aUserInfo.getId() != null) ApplicationPreferences.getInstance().setProperty( "user.id",  aUserInfo.getId());
   if(aUserInfo.getLocation() != null) ApplicationPreferences.getInstance().setProperty( "user.location",  aUserInfo.getLocation());
   if(aUserInfo.getTelNr() != null) ApplicationPreferences.getInstance().setProperty( "user.telnr",  aUserInfo.getTelNr());
  }

}
