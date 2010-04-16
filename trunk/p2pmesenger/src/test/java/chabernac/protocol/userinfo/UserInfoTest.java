/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.userinfo;

import java.util.Observable;
import java.util.Observer;

import chabernac.protocol.userinfo.UserInfo.Status;

import junit.framework.TestCase;

public class UserInfoTest extends TestCase {


  public void testUserInfo(){
    UserInfo theUserInfo = new UserInfo();
    
    theUserInfo.setId( "id" );
    assertEquals( "id", theUserInfo.getId() );
    
    theUserInfo.setName( "Guy Chauliac" );
    assertEquals( "Guy Chauliac", theUserInfo.getName() );
    
    theUserInfo.setLocation( "Borgerhout" );
    assertEquals( "Borgerhout", theUserInfo.getLocation());
    
    theUserInfo.setTelNr( "0486331565" );
    assertEquals( "0486331565", theUserInfo.getTelNr() );
    
    theUserInfo.setStatus( Status.AWAY );
    assertEquals( Status.AWAY, theUserInfo.getStatus() );
    
    ObserverCallCounter theCounter = new ObserverCallCounter();
    theUserInfo.addObserver( theCounter );
    theUserInfo.setId( "id2" );
    theUserInfo.setName( "Chauliac Guy" );
    theUserInfo.setLocation( "Berchem" );
    theUserInfo.setTelNr( "032351169" );
    theUserInfo.setStatus( Status.BUSY );
    
    assertEquals( 5, theCounter.getCounter() );
  }
  
  public class ObserverCallCounter implements Observer {
    private int myCounter = 0;
    
    @Override
    public void update( Observable anO, Object anArg ) {
      System.out.println(anArg);
      myCounter++;
    }
    
    public int getCounter(){
      return myCounter;
    }
  }

}
