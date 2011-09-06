/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.jni;

import java.awt.AWTException;
import java.awt.Robot;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class UserPresenceTest extends TestCase {
  
  public void testUserPresence() throws InterruptedException, AWTException{
    Robot theRobot = new Robot();
    //to mouse move to make sure a user event was triggered
    theRobot.mouseMove( 100, 100 );
    
    UserPresenceDetector theDetector = new UserPresenceDetector( 5, 10 );
    UserPresenceListener theListener = new UserPresenceListener();
    theDetector.addListener( theListener );
    theDetector.start();
    assertEquals( UserState.ONLINE, theDetector.getUserState());
    Thread.sleep( 2000 );
    assertEquals( 1, theListener.getUserStates().size() );
    assertEquals( UserState.ONLINE, theListener.getUserStates().get(0));
    
    Thread.sleep( 5000 );
    
    assertEquals( 2, theListener.getUserStates().size() );
    assertEquals( UserState.IDLE, theListener.getUserStates().get(1));
    
    Thread.sleep( 5000 );
    
    assertEquals( 3, theListener.getUserStates().size() );
    assertEquals( UserState.AWAY, theListener.getUserStates().get(2));
    
  }
  
  private class UserPresenceListener implements iUserPresenceListener{
    private List<UserState> myUserStates = new ArrayList<UserState>();

    @Override
    public void userStateChanged( UserState aState ) {
      myUserStates.add(aState);
    }
    
    public List<UserState> getUserStates(){
      return myUserStates;
    }
  }
}
