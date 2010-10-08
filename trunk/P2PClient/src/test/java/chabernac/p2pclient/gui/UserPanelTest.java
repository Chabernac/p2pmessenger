/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pclient.gui;

import java.awt.Color;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;

import chabernac.protocol.facade.P2PFacade;
import chabernac.protocol.facade.P2PFacadeException;
import chabernac.protocol.userinfo.DefaultUserInfoProvider;
import chabernac.protocol.userinfo.UserInfo.Status;

public class UserPanelTest extends TestCase {
  static{
    BasicConfigurator.resetConfiguration();
    BasicConfigurator.configure();
  }
  
  public void testUserPanel() throws P2PFacadeException, InterruptedException{
    DefaultUserInfoProvider theUserInfoProvider1 = new DefaultUserInfoProvider();
    theUserInfoProvider1.setEMail( "1@a.b" );
    theUserInfoProvider1.setId( "1" );
    theUserInfoProvider1.setName( "name 1" );
    theUserInfoProvider1.setTelNr( "1111 111 11 11" );
    
    P2PFacade theFacade1 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .setUserInfoProvider( theUserInfoProvider1 )
    .start( 5 );
    
    
    DefaultUserInfoProvider theUserInfoProvider2 = new DefaultUserInfoProvider();
    theUserInfoProvider2.setEMail( "2@a.b" );
    theUserInfoProvider2.setId( "2" );
    theUserInfoProvider2.setName( "name 2" );
    theUserInfoProvider2.setTelNr( "2222 222 22 22" );
    theUserInfoProvider2.setStatus( Status.BUSY );

    P2PFacade theFacade2 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .setUserInfoProvider( theUserInfoProvider2 )
    .start( 5 );
    
    
    DefaultUserInfoProvider theUserInfoProvider3 = new DefaultUserInfoProvider();
    theUserInfoProvider2.setEMail( "3@a.b" );
    theUserInfoProvider2.setId( "3" );
    theUserInfoProvider2.setName( "name 3" );
    theUserInfoProvider2.setTelNr( "3333 333 33 33" );
    theUserInfoProvider2.setStatus( Status.AWAY );

    P2PFacade theFacade3 = new P2PFacade()
    .setExchangeDelay( 300 )
    .setPersist( false )
    .setUserInfoProvider( theUserInfoProvider3 )
    .start( 5 );
    
    Thread.sleep( 5000 );
    
    ChatMediator theMediator = new ChatMediator(theFacade1);
    
    UserPanel theUserPanel = new UserPanel(theMediator);
    Map<String, StatusCheckBox>  theCheckBoxes = theUserPanel.getCheckBoxes();
    
//    assertTrue( theCheckBoxes.containsKey( theFacade1.getPeerId() ) );
    assertTrue( theCheckBoxes.containsKey( theFacade2.getPeerId() ) );
    assertTrue( theCheckBoxes.containsKey( theFacade3.getPeerId() ) );
    
//    assertEquals( "1 1@a.b 1111 111 11 11", theCheckBoxes.get( theFacade1.getPeerId() ).getToolTipText());
    assertEquals( "BUSY name 2 2 2@a.b 2222 222 22 22", theCheckBoxes.get( theFacade2.getPeerId() ).getToolTipText());
    assertEquals( "AWAY name 3 3 3@a.b 3333 333 33 33", theCheckBoxes.get( theFacade2.getPeerId() ).getToolTipText());
    
    assertEquals( new Color(0,200,0), theCheckBoxes.get( theFacade2.getPeerId() ).getForeground());
    
    Set<String> theSelectedUsers = new HashSet< String >();
    theSelectedUsers.add(theFacade1.getPeerId());
    theUserPanel.setSelectedUsers( theSelectedUsers );
    
    assertTrue( theUserPanel.getCheckBoxes().get( theFacade1.getPeerId() ).isSelected() );
    
    Set<String> theSelected = theUserPanel.getSelectedUsers();
    
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
