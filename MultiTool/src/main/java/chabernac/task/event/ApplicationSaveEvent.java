package chabernac.task.event;

import chabernac.event.Event;

public class ApplicationSaveEvent extends Event {

  public ApplicationSaveEvent() {
    super("Application closing");
  }

}
