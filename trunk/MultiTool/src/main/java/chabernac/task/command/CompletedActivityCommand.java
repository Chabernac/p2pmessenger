package chabernac.task.command;

import chabernac.task.Task;

public class CompletedActivityCommand extends ActivityCommand {
  
  CompletedActivityCommand(){
    super();
  }

  public void executeCommand() {
    Task theSelectedTask = getSelectedTask();
    if(theSelectedTask != null){
      theSelectedTask.setCompleted(!theSelectedTask.isCompleted());
    }
    update();
    goToTask(theSelectedTask);
  }
  
  public String getName(){
    Task theSelectedTask = getSelectedTask();
    if(theSelectedTask == null){
      return "Set completed";
    } else {
      if(theSelectedTask.isCompleted()) return "Set not completed";
      else return "Set completed";
    }
  }  
  
  public boolean isEnabled(){
    Task theSelectedTask = getSelectedTask();
    if(theSelectedTask == null) return false;
    return true;
  }
  
  public char getMnemonic(){
    return 'd';
  }
}
