package chabernac.messengerservice.event;

import chabernac.messengerservice.MessengerUser;

public class UserChangedEvent extends UserEvent {

  public UserChangedEvent(MessengerUser aUser) {
    super("User change: " + aUser.getId(), aUser);
  }
}
