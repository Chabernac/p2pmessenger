package chabernac.messengerservice.event;

import chabernac.chat.Message;

public class MessageSendEvent extends MessageEvent{

  public MessageSendEvent(Message aMessage) {
    super("The message with envelop: "  + aMessage.getEnvelop() + " was send", aMessage);
  }

}
