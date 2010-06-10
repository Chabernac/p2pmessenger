package chabernac.chat.gui.event;


import java.util.List;

import chabernac.event.Event;

public class SelectUsersEvent extends Event{
  private List myUserList = null;

  public SelectUsersEvent(List aUserList) {
    super("Selecting users");
    myUserList = aUserList;
  }

  public List getUserList() {
    return myUserList;
  }

  public void setUserList(List anUserList) {
    myUserList = anUserList;
  }
}
