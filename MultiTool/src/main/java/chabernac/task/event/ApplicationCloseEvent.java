package chabernac.task.event;

import chabernac.event.Event;

public class ApplicationCloseEvent extends Event {

  public ApplicationCloseEvent() {
    super("Application close event");
  }

}
