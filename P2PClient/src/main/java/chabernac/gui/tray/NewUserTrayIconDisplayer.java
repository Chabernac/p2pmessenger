/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui.tray;

import java.util.HashMap;
import java.util.Map;

import chabernac.p2pclient.gui.ChatMediator;
import chabernac.protocol.userinfo.UserInfo;
import chabernac.protocol.userinfo.iUserInfoListener;

public class NewUserTrayIconDisplayer extends TrayIconAnimator{
  NewUserTrayIconDisplayer(){
  }

  public NewUserTrayIconDisplayer ( ChatMediator anMediator ) throws Exception {
    super(anMediator, "images/message.png", "images/message2.png", 60);
  }

  private Map<String, UserInfo.Status> myLatestUserInfo = new HashMap<String, UserInfo.Status>();


  public Map<String, UserInfo.Status> getLatestUserInfo() {
    return myLatestUserInfo;
  }

  public void setLatestUserInfo( Map<String, UserInfo> aLatestUserInfo ) {
    //we should take a copy of the map otherwise we do not see differences between the newly given map and the previous version
    
    myLatestUserInfo.clear();
    for(String theUser : aLatestUserInfo.keySet()){
     myLatestUserInfo.put( theUser, aLatestUserInfo.get( theUser ).getStatus() ); 
    }
  }


  public class MyUserListener implements iUserInfoListener {

    @Override
    public void userInfoChanged( UserInfo aUserInfo, Map<String, UserInfo> aFullUserInfoList ) {
      checkUserInfo( aFullUserInfoList );
      setLatestUserInfo( aFullUserInfoList );
    }
  }

  private void checkUserInfo(Map<String, UserInfo> aFullUserInfoList){
    if(isAnimationRequired( aFullUserInfoList )){
      animate();
    }
  }

  boolean isAnimationRequired(Map<String, UserInfo> aFullUserInfoList){
    for(String theUserId : aFullUserInfoList.keySet()){
      UserInfo theNewUserInfo = aFullUserInfoList.get(theUserId);
      //only if the new user info changed to an online status
      if(theNewUserInfo.getStatus() != UserInfo.Status.OFFLINE) {
        //only if the user was not known yet or the status was previously offline
        if(!myLatestUserInfo.containsKey( theUserId ) || myLatestUserInfo.get( theUserId ) == UserInfo.Status.OFFLINE){
          //if we have detected one user change stop further processing
          return true;
        }
      }
    }
    return false;
  }


  @Override
  protected void addListeners() throws Exception {
    myMediator.getP2PFacade().addUserInfoListener( new MyUserListener() );
  }
}
