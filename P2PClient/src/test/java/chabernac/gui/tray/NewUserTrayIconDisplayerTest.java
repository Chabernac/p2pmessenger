/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui.tray;

import java.util.HashMap;
import java.util.Map;

import chabernac.protocol.userinfo.UserInfo;

import junit.framework.TestCase;

public class NewUserTrayIconDisplayerTest extends TestCase {
  public void testUserInfoChanged() throws Exception{
    NewUserTrayIconDisplayer theDisplayer = new NewUserTrayIconDisplayer( );
    
    Map<String, UserInfo> theUserInfo = new HashMap<String, UserInfo>();
    UserInfo theUserInfo1 = new UserInfo();
    theUserInfo1.setStatus( UserInfo.Status.ONLINE );
    theUserInfo.put( "1", theUserInfo1 );
    
    assertTrue( theDisplayer.isAnimationRequired( theUserInfo ) );
    theDisplayer.setLatestUserInfo( theUserInfo );
    assertFalse( theDisplayer.isAnimationRequired( theUserInfo ) );
    theDisplayer.setLatestUserInfo( theUserInfo );
    
    UserInfo theUserInfo2 = new UserInfo();
    theUserInfo2.setStatus( UserInfo.Status.ONLINE );
    theUserInfo.put( "2", theUserInfo2 );
    
    assertTrue( theDisplayer.isAnimationRequired( theUserInfo ) );
    theDisplayer.setLatestUserInfo( theUserInfo );
    
    theUserInfo2.setStatus( UserInfo.Status.OFFLINE );
    assertFalse( theDisplayer.isAnimationRequired( theUserInfo ) );
    theDisplayer.setLatestUserInfo( theUserInfo );
    
    theUserInfo2.setStatus( UserInfo.Status.ONLINE );
    assertTrue( theDisplayer.isAnimationRequired( theUserInfo ) );
    theDisplayer.setLatestUserInfo( theUserInfo );
  }
}
