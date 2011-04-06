/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pclient.gui;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import junit.framework.TestCase;
import chabernac.protocol.facade.P2PFacade;
import chabernac.protocol.facade.P2PFacadeException;
import chabernac.protocol.message.MultiPeerMessage;

public class NewMessageDialog5Test extends TestCase {
  public void testNewMessageDialog5() throws P2PFacadeException, InterruptedException, AWTException{
    MultiPeerMessage theMessage = MultiPeerMessage.createMessage( "test" );
    P2PFacade theFacade = new P2PFacade();
    theFacade.start( 1 );
    ChatMediator theMediator = new ChatMediator( theFacade );
    NewMessageDialog5.getInstance( theMediator ).showMessage( theMessage );
//    Thread.sleep(5000);
    Robot theRobot = new Robot();
    theRobot.keyPress( KeyEvent.VK_ALT );
    theRobot.keyPress( KeyEvent.VK_F4 );
    theRobot.keyRelease(  KeyEvent.VK_F4 );
    theRobot.keyPress( KeyEvent.VK_ALT );
    assertFalse( NewMessageDialog5.getInstance( theMediator ).isVisible() );
  }
}
