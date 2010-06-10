package chabernac.task.command;


import org.apache.log4j.Logger;

import chabernac.task.Task;
import chabernac.task.TaskException;
import chabernac.task.gui.TaskDialog;

public class CreateActivityCommand extends ActivityCommand{
  private static Logger logger = Logger.getLogger(CreateActivityCommand.class);
  
  CreateActivityCommand(){
    super();
  }

  public void executeCommand() {
    try{
      Task parent = getSelectedTask();
      if(parent == null) return;
      Task theTask = new Task(Task.GENERAL_TASK, "New activity");
      TaskDialog theTaskDialog = new TaskDialog(theTask);
      if(theTaskDialog.showTaskDialog()){
        parent.addSubTask(theTask);
        update();
        goToTask(theTask);
      } else {
        goToTask(parent);
      }
    }catch(TaskException e){
      logger.error("Task could not be added", e);
    }

  }

  public String getName() {
    return "Create activity";
  }

  public boolean isEnabled() {
    Task parent = getSelectedTask();
    if(parent == null) return false;
    if(parent.getPeriods().size() > 0) return false;
    return true;
  }

}
