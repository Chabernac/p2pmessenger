package chabernac.task.event;

import chabernac.event.Event;
import chabernac.task.Task;

public class TaskEvent extends Event {
  private final Task myTask;

  public TaskEvent(Task aTask, String aDescription) {
    super(aDescription );
    myTask = aTask;
  }
  
  public Task getTask(){
    return myTask;
  }
}
