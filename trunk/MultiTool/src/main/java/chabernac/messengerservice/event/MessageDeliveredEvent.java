package chabernac.messengerservice.event;

import chabernac.chat.Message;

public class MessageDeliveredEvent extends MessageEvent {
  public MessageDeliveredEvent(Message aMessage) {
    super("The message with envelop: "  + aMessage.getEnvelop() + " was delivered", aMessage);
  }
}
