package chabernac.chat.gui.event;

import chabernac.messengerservice.MessengerUser;
import chabernac.messengerservice.event.UserEvent;

public class UserAlreadyLoggedOnEvent extends UserEvent{
  
  public UserAlreadyLoggedOnEvent(MessengerUser aUser){
    super("User already logged on: " + aUser.getId(), aUser);
  }
}
