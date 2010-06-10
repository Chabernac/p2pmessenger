package chabernac.messengerservice.event;

import chabernac.messengerservice.MessengerUser;

public class UserRemovedEvent extends UserEvent {

  public UserRemovedEvent(MessengerUser aUser) {
    super("User removed: " + aUser.getId(), aUser);
  }

}
