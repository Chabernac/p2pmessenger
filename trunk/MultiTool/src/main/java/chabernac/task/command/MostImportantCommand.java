package chabernac.task.command;

import chabernac.task.Task;

public class MostImportantCommand extends ActivityCommand {
  
  MostImportantCommand(){
    super();
  }

  protected void executeCommand() {
    Task theMostImportant = (Task)getRootTask().getSortedTasks().elementAt(0);
    goToTask(theMostImportant);
  }

  public String getName() {
    return "Most important";
  }

  public boolean isEnabled() {
    return true;
  }

}
