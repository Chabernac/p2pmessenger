/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.reactorplugin;

import junit.framework.TestCase;

public class ScoreTest extends TestCase {
  public void testScore(){
    Score theScore = new Score();
    
    theScore.correctAnswer( "1" );
    theScore.correctAnswer( "2" );
    theScore.correctAnswer( "2" );
    theScore.wrongAnswer( "1" );
    theScore.wrongAnswer( "1" );
    
    assertEquals( -1, theScore.getScore( "1" ));
    assertEquals( 2, theScore.getScore( "2" ));
    assertEquals( 0, theScore.getScore( "3" ));
  }
}
