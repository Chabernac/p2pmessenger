/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.ldapuserinfoprovider;

import chabernac.preference.ApplicationPreferences;
import chabernac.protocol.userinfo.UserInfo;
import chabernac.protocol.userinfo.UserInfoException;
import junit.framework.TestCase;

public class BackupUserInfoProviderDecoratorTest extends TestCase {
  public void testBackup() throws UserInfoException{
    ApplicationPreferences thePreferences = ApplicationPreferences.getInstance();
    
    thePreferences.setProperty( "user.name", "Guy Chauliac" );
    thePreferences.setProperty( "user.telnr", "0486331565");
    thePreferences.setProperty( "user.location", "home" );
    thePreferences.setProperty( "user.email", "guy.chauliac@gmail.com" );
    
    FailingUserInfoProvider theUserInfoProvider = new FailingUserInfoProvider();
    BackupUserInfoProviderDecorator theDecorator = new BackupUserInfoProviderDecorator(theUserInfoProvider);
    
    UserInfo theUserInfo = theDecorator.getUserInfo();
    
    assertEquals( "Guy Chauliac", theUserInfo.getName() );
    assertEquals( "0486331565", theUserInfo.getTelNr() );
    assertEquals( "home", theUserInfo.getLocation() );
    assertEquals( "guy.chauliac@gmail.com", theUserInfo.getEMail() );
    assertEquals( "DGCH804", theUserInfo.getId() );
    
    theUserInfo.setEMail( "abc@gmail.com" );
    theUserInfo.setName( "Jef Patat" );
    theUserInfo.setLocation( "somewhere" );
    theUserInfo.setTelNr( "123" );
    theUserInfo.setId( "myid" );
    
    theUserInfoProvider.setUserInfo( theUserInfo );
    
    theUserInfo = theDecorator.getUserInfo();
    
    //the application preferences should have changed now, check it
    
    assertEquals( "Jef Patat", thePreferences.getProperty( "user.name" ));
    assertEquals( "123", thePreferences.getProperty( "user.telnr" ));
    assertEquals( "somewhere", thePreferences.getProperty( "user.location" ));
    assertEquals( "abc@gmail.com", thePreferences.getProperty( "user.email" ));
    assertEquals( "myid", thePreferences.getProperty( "user.id" ));
    
    
  }
}
