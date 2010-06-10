package chabernac.chat.gui.event;

import java.util.Map;

import chabernac.event.Event;

public class TotalUserListChangedEvent extends Event {
  private Map myUserList = null;
  
  public TotalUserListChangedEvent(Map aUserList){
    super("User list changed");
    myUserList = aUserList;
  }

  public Map getUserList() {
    return myUserList;
  }

  public void setUserList(Map anUserList) {
    myUserList = anUserList;
  }
}
