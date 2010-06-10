package chabernac.messengerservice.event;

import chabernac.chat.Message;

public class MessageStatusEvent extends MessageEvent {
  private String myUser = null;
  private String myOldStatus = null;
  private String myNewStatus = null;

  public MessageStatusEvent(Message aMessage, String aUser, String anOldStatus, String aNewStatus) {
    super("Status change event", aMessage);
    myUser = aUser;
    myOldStatus = anOldStatus;
    myNewStatus = aNewStatus;
  }

  public String getNewStatus() {
    return myNewStatus;
  }

  public void setNewStatus(String anNewStatus) {
    myNewStatus = anNewStatus;
  }

  public String getOldStatus() {
    return myOldStatus;
  }

  public void setOldStatus(String anOldStatus) {
    myOldStatus = anOldStatus;
  }

  public String getUser() {
    return myUser;
  }

  public void setUser(String anUser) {
    myUser = anUser;
  }
  
  

}
