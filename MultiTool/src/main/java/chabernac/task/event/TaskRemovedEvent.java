package chabernac.task.event;

import chabernac.task.Task;

public class TaskRemovedEvent extends TaskEvent {

  public TaskRemovedEvent(Task aTask) {
    super(aTask, "Task removed: " + aTask.getDescription() );
  }
}
