package chabernac.messengerservice.event;

import chabernac.chat.Message;

public class MessageSelectedEvent extends MessageEvent {

  public MessageSelectedEvent(Message aMessage) {
    super("Message selected: " + (aMessage == null ? "null" : aMessage.getEnvelop()), aMessage);
  }

}
