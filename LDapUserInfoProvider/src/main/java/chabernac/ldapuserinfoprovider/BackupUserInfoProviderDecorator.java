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


  public void fillUserInfo( UserInfo aUserInfo ) throws UserInfoException {
    try {
      myDelegate.fillUserInfo( aUserInfo );
      saveBackup(aUserInfo);
    }catch(UserInfoException e){
      LOGGER.error( "Faild to retrieve user info with class '" + myDelegate.getClass() + "' trying to load backup", e );
      loadBackup(aUserInfo);
    }
  }


  private void loadBackup(UserInfo aUserInfo) {
    ApplicationPreferences thePrefereces = ApplicationPreferences.getInstance();
    if(thePrefereces.containsKey( "user.email" )) aUserInfo.setEMail( thePrefereces.getProperty( "user.email" ) );
    aUserInfo.setName( thePrefereces.getProperty( "user.name", System.getProperty( "user.name" ) ));
    aUserInfo.setId( thePrefereces.getProperty( "user.id", System.getProperty( "user.name" ) ));
    if(thePrefereces.containsKey( "user.location" )) aUserInfo.setLocation( thePrefereces.getProperty( "user.location"));
    if(thePrefereces.containsKey( "user.telnr" )) aUserInfo.setTelNr( thePrefereces.getProperty( "user.telnr"));
  }


  private void saveBackup(UserInfo aUserInfo) {
   if(aUserInfo.getName() != null) ApplicationPreferences.getInstance().setProperty( "user.name",  aUserInfo.getName());
   if(aUserInfo.getEMail() != null) ApplicationPreferences.getInstance().setProperty( "user.email",  aUserInfo.getEMail());
   if(aUserInfo.getId() != null) ApplicationPreferences.getInstance().setProperty( "user.id",  aUserInfo.getId());
   if(aUserInfo.getLocation() != null) ApplicationPreferences.getInstance().setProperty( "user.location",  aUserInfo.getLocation());
   if(aUserInfo.getTelNr() != null) ApplicationPreferences.getInstance().setProperty( "user.telnr",  aUserInfo.getTelNr());
  }
}
