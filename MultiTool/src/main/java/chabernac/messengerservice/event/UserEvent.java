package chabernac.messengerservice.event;

import chabernac.event.Event;
import chabernac.messengerservice.MessengerUser;

public class UserEvent extends Event{
  private MessengerUser myUser = null;
  
  public UserEvent(String aDescription, MessengerUser aUser){
    super(aDescription);
    myUser = aUser;
  }

  public MessengerUser getUser() {
    return myUser;
  }

  public void setUser(MessengerUser anUser) {
    myUser = anUser;
  }
  
  

}
