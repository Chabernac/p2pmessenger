/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.ldapuserinfoprovider;

import junit.framework.TestCase;
import chabernac.protocol.userinfo.UserInfo;
import chabernac.protocol.userinfo.UserInfoException;

public class AAXLDAPUserInfoProviderTest extends TestCase {
  public void testUserInfoProvider() throws UserInfoException{
    AXALDAPUserInfoProvider theProvider = new AXALDAPUserInfoProvider();
    UserInfo theInfo = theProvider.getUserInfo();
    assertEquals( "Guy Chauliac", theInfo.getName() );
    assertEquals( "guy.chauliac@axa.be", theInfo.getEMail() );
    assertEquals( "+32 (0)3 286 25 82", theInfo.getTelNr() );
    assertEquals( "DGCH804", theInfo.getId());
    
  }
}
