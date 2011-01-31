package chabernac.task.event;

import chabernac.event.Event;
import chabernac.task.Task;

public class TaskStartedEvent extends Event {
  private Task myTask = null;

  public TaskStartedEvent(Task aTask) {
    super("Task started: " + aTask.getFullName() );
    myTask = aTask;
  }
  
  public Task getTask(){
    return myTask;
  }
}
