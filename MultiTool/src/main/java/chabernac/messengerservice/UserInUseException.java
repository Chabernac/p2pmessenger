package chabernac.messengerservice;

public class UserInUseException extends Exception {
  private MessengerUser myUser = null;

  private static final long serialVersionUID = 6727459811894401564L;

  public UserInUseException() {
  }

  public UserInUseException(String message) {
    super(message);
  }

  public UserInUseException(Throwable cause) {
    super(cause);
  }

  public UserInUseException(String message, Throwable cause) {
    super(message, cause);
  }

  public UserInUseException(String string, MessengerUser aUser) {
   this(string);
   myUser = aUser;
  }
  
  public MessengerUser getUser(){
    return myUser;
  }
}
