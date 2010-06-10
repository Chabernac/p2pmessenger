package chabernac.task.event;

import chabernac.event.Event;
import chabernac.task.Task;

public class TaskSelectedEvent extends Event {
  private Task myTask = null;

  public TaskSelectedEvent(Task aTask) {
    super("Task selected: " + aTask.getFullName() );
    myTask = aTask;
  }
  
  public Task getTask(){
    return myTask;
  }

}
