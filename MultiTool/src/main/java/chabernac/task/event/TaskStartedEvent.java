package chabernac.task.event;

import chabernac.task.Task;

public class TaskStartedEvent extends TaskEvent{

  public TaskStartedEvent(Task aTask) {
    super(aTask, "Task started: " + aTask.getFullName() );
  }
}
