package chabernac.messengerservice.event;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import chabernac.event.Event;

public class UserListChangedEvent extends Event {
  private Map myUserList = null;
  
  public UserListChangedEvent(Map aUserList){
    super("User list changed");
    //Give a copy to the client, the client should not be able to modify the map
    myUserList = Collections.unmodifiableMap(new HashMap(aUserList));
  }

  public Map getUserList() {
    return myUserList;
  }

  public void setUserList(Map anUserList) {
    myUserList = anUserList;
  }
  
  
}
