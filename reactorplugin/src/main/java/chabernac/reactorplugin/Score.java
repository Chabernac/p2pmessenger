/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.reactorplugin;

import java.util.HashMap;
import java.util.Map;

public class Score {
  private Map<String, Integer> myScore = new HashMap<String, Integer>();
  
  public synchronized void correctAnswer(String aPlayerId){
    initPlayer( aPlayerId );
    myScore.put( aPlayerId, myScore.get(aPlayerId).intValue() + 1);
  }
  
  public synchronized void wrongAnswer(String aPlayerId){
    initPlayer( aPlayerId );
    myScore.put( aPlayerId, myScore.get(aPlayerId).intValue() - 1);
  }
  
  private void initPlayer(String aPlayerId){
    if(!myScore.containsKey( aPlayerId )){
      myScore.put( aPlayerId, new Integer(0) );
    }
  }
  
  public int getScore(String aPlayer){
    if(!myScore.containsKey( aPlayer )) return 0;
    return myScore.get(aPlayer).intValue();
  }
}
