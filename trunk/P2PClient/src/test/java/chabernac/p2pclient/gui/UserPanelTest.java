/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pclient.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import chabernac.protocol.facade.P2PFacade;
import chabernac.protocol.facade.P2PFacadeException;
import chabernac.protocol.userinfo.DefaultUserInfoProvider;
import chabernac.protocol.userinfo.UserInfo.Status;

public class UserPanelTest extends TestCase {
  public void testUserPanel() throws P2PFacadeException, InterruptedException{
    DefaultUserInfoProvider theUserInfoProvider1 = new DefaultUserInfoProvider();
    theUserInfoProvider1.getUserInfo().setEMail( "1@a.b" );
    theUserInfoProvider1.getUserInfo().setId( "1" );
    theUserInfoProvider1.getUserInfo().setName( "name 1" );
    theUserInfoProvider1.getUserInfo().setTelNr( "1111 111 11 11" );
    
    
    P2PFacade theFacade1 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .setUserInfoProvider( theUserInfoProvider1 )
    .start( 5 );
    
    DefaultUserInfoProvider theUserInfoProvider2 = new DefaultUserInfoProvider();
    theUserInfoProvider2.getUserInfo().setEMail( "2@a.b" );
    theUserInfoProvider2.getUserInfo().setId( "2" );
    theUserInfoProvider2.getUserInfo().setName( "name 2" );
    theUserInfoProvider2.getUserInfo().setTelNr( "2222 222 22 22" );
    theUserInfoProvider2.getUserInfo().setStatus( Status.BUSY );

    P2PFacade theFacade2 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .setUserInfoProvider( theUserInfoProvider2 )
    .start( 5 );
    
    Thread.sleep( 3000 );
    
    ChatMediator theMediator = new ChatMediator(theFacade1);
    
    UserPanel theUserPanel = new UserPanel(theMediator);
    Map<String, StatusCheckBox>  theCheckBoxes = theUserPanel.getCheckBoxes();
    
    assertTrue( theCheckBoxes.containsKey( theFacade1.getPeerId() ) );
    assertTrue( theCheckBoxes.containsKey( theFacade2.getPeerId() ) );
    
    assertEquals( "1 1@a.b 1111 111 11 11", theCheckBoxes.get( theFacade1.getPeerId() ).getToolTipText());
    assertEquals( "2 2@a.b 2222 222 22 22", theCheckBoxes.get( theFacade2.getPeerId() ).getToolTipText());
    
    assertEquals( new Color(0,200,0), theCheckBoxes.get( theFacade2.getPeerId() ).getForeground());
    
    List<String> theSelectedUsers = new ArrayList< String >();
    theSelectedUsers.add(theFacade1.getPeerId());
    theUserPanel.setSelectedUsers( theSelectedUsers );
    
    assertTrue( theUserPanel.getCheckBoxes().get( theFacade1.getPeerId() ).isSelected() );
    
    List<String> theSelected = theUserPanel.getSelectedUsers();
    
    assertEquals( theSelectedUsers, theSelected );
    
    theSelectedUsers.add( theFacade2.getPeerId() );
    theUserPanel.setSelectedUsers( theSelectedUsers );
    
    assertTrue( theUserPanel.getCheckBoxes().get( theFacade2.getPeerId() ).isSelected() );
    
    theSelected = theUserPanel.getSelectedUsers();
    
    assertEquals( theSelectedUsers.size(), theSelected.size() );
    for(String theUser : theSelectedUsers){
      assertTrue( theSelected.contains( theUser ) );
    }
    
    theSelectedUsers.clear();
    theSelectedUsers.add( theFacade2.getPeerId() );
    
    theUserPanel.setSelectedUsers( theSelectedUsers );
    theSelected = theUserPanel.getSelectedUsers();
    assertEquals( 1, theSelected.size() );
    assertFalse( theSelected.contains( theFacade1.getPeerId() ) );
    assertTrue( theSelected.contains( theFacade2.getPeerId() ) );
  }
  
}
