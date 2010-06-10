package chabernac.messengerservice.event;

import chabernac.messengerservice.MessengerUser;

public class LoggedOnEvent extends UserEvent {
  
  public LoggedOnEvent(MessengerUser aUser){
    super("User logged on: " + aUser.getId(), aUser);
  }
}
