/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.jna;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UserPresenceDetector {
  private List<iUserPresenceListener> myListeners = new ArrayList<iUserPresenceListener>();
  private ScheduledExecutorService myExecutorService = null;
  private int myIdleTimeoutInSeconds;
  private int myAwayTimeoutInSeconds;
  private int myRefreshTimeInSeconds;


  public UserPresenceDetector(int anIdleTimeoutInSeconds, int anAwayTimeoutInSeconds, int aRefreshTimeInSeconds){
    myIdleTimeoutInSeconds = anIdleTimeoutInSeconds;
    myAwayTimeoutInSeconds= anAwayTimeoutInSeconds;
    myRefreshTimeInSeconds = aRefreshTimeInSeconds;
  }

  public void addListener(iUserPresenceListener aListener){
    myListeners.add(aListener);
  }

  public void removeListener(iUserPresenceListener aListener){
    myListeners.remove( aListener );
  }

  public void start(){
    if(myExecutorService == null){
      myExecutorService = Executors.newScheduledThreadPool( 1 );
      myExecutorService.scheduleAtFixedRate( new PollingThread(), myRefreshTimeInSeconds, myRefreshTimeInSeconds, TimeUnit.SECONDS );
    }
  }

  public void stop(){
    myExecutorService.shutdownNow();
    myExecutorService = null;
  }

  public UserState getUserState(){
    int idleSec = Win32IdleTime.getIdleTimeMillisWin32() / 1000;

    UserState theUserState =
      idleSec < myIdleTimeoutInSeconds ? UserState.ONLINE :
        idleSec > myAwayTimeoutInSeconds ? UserState.AWAY : UserState.IDLE;
        return theUserState;
  }


  private class PollingThread implements Runnable{
    private UserState myCurrentState = UserState.UNKNOWN;

    @Override
    public void run() {

      UserState theNewState = getUserState();
      if (theNewState != myCurrentState) {
        myCurrentState = theNewState;
        for(iUserPresenceListener theListener : myListeners){
          theListener.userStateChanged( myCurrentState );
        }
      }
    }

  }
} 
