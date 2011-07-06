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
  public NewUserTrayIconDisplayer ( ChatMediator anMediator ) throws Exception {
    super(anMediator, "images/message.png", "images/message2.png", 30);
  }
  
  private Map<String, UserInfo> myLatestUserInfo = new HashMap<String, UserInfo>();
  

  public class MyUserListener implements iUserInfoListener {

    @Override
    public void userInfoChanged( UserInfo aUserInfo, Map<String, UserInfo> aFullUserInfoList ) {
      for(String theUserId : aFullUserInfoList.keySet()){
        UserInfo theNewUserInfo = aFullUserInfoList.get(theUserId);
        //only if the new user info changed to an online status
        if(theNewUserInfo.getStatus() != UserInfo.Status.OFFLINE) {
          //only if the user was not known yet or the status was previously offline
          if(!myLatestUserInfo.containsKey( theUserId ) || myLatestUserInfo.get( theUserId ).getStatus() == UserInfo.Status.OFFLINE){
            animate();
            //if we have detected one user change stop further processing
            return;
          }
        }
        
      }
      myLatestUserInfo = aFullUserInfoList;
    }
  }
  

  @Override
  protected void addListeners() throws Exception {
    myMediator.getP2PFacade().addUserInfoListener( new MyUserListener() );
  }
}
