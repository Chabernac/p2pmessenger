package chabernac.task.command;

import chabernac.task.Task;
import chabernac.task.gui.TaskDialog;

public class ModifyActivityCommand extends ActivityCommand {
  
  ModifyActivityCommand(){
    super();
  }

  public void executeCommand() {
    Task theSelectedTask = getSelectedTask();
    if(theSelectedTask != null){
      TaskDialog theDialog = new TaskDialog(theSelectedTask);
      theDialog.showTaskDialog();
    }
    update();
    goToTask(theSelectedTask);
  }

  public String getName() {
    return "Modify activity";
  }

  public boolean isEnabled() {
    Task theSelectedTask = getSelectedTask();
    if(theSelectedTask == null) return false;
    return true;
  }

}
