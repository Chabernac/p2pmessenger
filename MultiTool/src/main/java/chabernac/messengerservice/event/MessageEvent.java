package chabernac.messengerservice.event;

import chabernac.chat.Message;
import chabernac.event.Event;

public class MessageEvent extends Event {
  private Message myMessage = null;

  public MessageEvent(String anDescription, Message aMessage) {
    super(anDescription);
    myMessage = aMessage;
  }

  public Message getMessage() {
    return myMessage;
  }

  public void setMessage(Message anMessage) {
    myMessage = anMessage;
  }
  
  

}
