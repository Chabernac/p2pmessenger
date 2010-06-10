package chabernac.messengerservice.event;

import chabernac.messengerservice.MessengerUser;

public class UserInfoSendEvent extends UserEvent {

  public UserInfoSendEvent(MessengerUser aUser) {
    super("User info send", aUser);
  }
}
