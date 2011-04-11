/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.reactorplugin;

import junit.framework.TestCase;

public class ReactionTest extends TestCase {
  public void testReactionTest(){
    Reaction theReaction = new Reaction( new PlusMinStatement( "+", 3, 6, 9 ) );
    
    assertFalse( theReaction.isFirstCorrectAnswer( "1", false ) );
    assertTrue( theReaction.isFirstCorrectAnswer( "2", true) );
    assertFalse( theReaction.isFirstCorrectAnswer( "2", false) );
    assertFalse( theReaction.isFirstCorrectAnswer( "3", false) );
    
    assertEquals( "2", theReaction.getWinner() );
    
    assertTrue( theReaction.getResponseTime( "1" ) > 0 );
    assertTrue( theReaction.getResponseTime( "2" ) > 0 );
    assertTrue( theReaction.getResponseTime( "3" ) > 0 );
    assertTrue( theReaction.getResponseTime( ) > 0 );
    assertEquals( -1, theReaction.getResponseTime( "4" ));
    
    assertEquals( 3, theReaction.getPlayers().size() );
    
    assertTrue( theReaction.getPlayers().contains( "1" ) );
    assertTrue( theReaction.getPlayers().contains( "2" ) );
    assertTrue( theReaction.getPlayers().contains( "3" ) );
    assertFalse( theReaction.getPlayers().contains( "4" ) );
    
    
    
  }
}
