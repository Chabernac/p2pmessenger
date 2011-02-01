package chabernac.task.event;

import chabernac.task.Task;

public class TaskSelectedEvent extends TaskEvent {

  public TaskSelectedEvent(Task aTask) {
    super(aTask, "Task selected: " + aTask.getFullName() );
  }
}
