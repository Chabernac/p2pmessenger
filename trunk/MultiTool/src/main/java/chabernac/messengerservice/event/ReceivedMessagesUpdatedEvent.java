package chabernac.messengerservice.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import chabernac.event.Event;

public class ReceivedMessagesUpdatedEvent extends Event {
  private List myMessages = null;

  public ReceivedMessagesUpdatedEvent(ArrayList aMessages) {
    super("Received messages updated");
    myMessages = Collections.unmodifiableList(new ArrayList(aMessages));
  }

  public List getMessages() {
    return myMessages;
  }

  public void setMessages(List anMessages) {
    myMessages = anMessages;
  }
  
  

}
