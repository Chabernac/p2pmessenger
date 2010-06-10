package chabernac.messengerservice.event;

import chabernac.messengerservice.MessengerUser;

public class LoggedOffEvent extends UserEvent{
  
  public LoggedOffEvent(MessengerUser aUser) {
    super("User logged off: " + aUser.getId(), aUser);
  }
}
