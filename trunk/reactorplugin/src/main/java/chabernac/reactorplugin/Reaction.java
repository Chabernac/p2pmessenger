/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.reactorplugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Reaction {
  private final AbstractStatement myStatement;
  private long myStartTime = System.nanoTime();
  private long myEndTime = -1;
  
  private Map<String, Long> myResponseTimes = new HashMap<String, Long>();
  
  private String myWinnerId = null;

  public Reaction( AbstractStatement aStatement ) {
    super();
    myStatement = aStatement;
  }
  
  public synchronized boolean isFirstCorrectAnswer(String aUserid, boolean aResult){
    if(!myResponseTimes.containsKey( aUserid )){
      myResponseTimes.put( aUserid, System.nanoTime() );
    }
    
    if(myEndTime != -1) {
      return false;
    }
    
    if(myStatement.isTrue() == aResult){
      myEndTime = System.nanoTime();
      myWinnerId = aUserid;
      return true;
    }
    
    return false;
  }
  
  public String getWinner(){
    return myWinnerId;
  }
  
  public long getResponseTime(){
    if(myEndTime == -1)  return -1;
    return myEndTime - myStartTime;
  }
  
  public Set<String> getPlayers(){
    return myResponseTimes.keySet();
  }
  
  public long getResponseTime(String aPlayer){
    if(!myResponseTimes.containsKey( aPlayer )) return -1;
    return myResponseTimes.get(aPlayer);
  }
}
