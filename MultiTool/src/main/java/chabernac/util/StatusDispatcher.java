package chabernac.util;

import chabernac.event.ApplicationEventDispatcher;


public class StatusDispatcher {
  
  public static void showWarning(String aWarning){
    ApplicationEventDispatcher.fireEvent(new StatusEvent(StatusEvent.WARNING, aWarning));
  }
  
  public static void showError(String anError){
    ApplicationEventDispatcher.fireEvent(new StatusEvent(StatusEvent.ERROR, anError));
  }
  
  public static void showMessage(String aMessage){
    ApplicationEventDispatcher.fireEvent(new StatusEvent(StatusEvent.MESSAGE, aMessage));
  }
}
