package chabernac.messengerservice.event;

import chabernac.chat.Message;

public class MessageReceivedEvent extends MessageEvent {
  
  public MessageReceivedEvent(Message aMessage){
    super("Message received event", aMessage);
  }
}
