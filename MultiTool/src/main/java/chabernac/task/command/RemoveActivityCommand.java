package chabernac.task.command;

import org.apache.log4j.Logger;

import chabernac.task.Task;
import chabernac.task.TaskException;

public class RemoveActivityCommand extends ActivityCommand {
  private static Logger logger = Logger.getLogger(RemoveActivityCommand.class);
  
  RemoveActivityCommand(){
    super();
  }

  public void executeCommand() {
    try{
      if(getSelectedTask() != null){
        Task parent = getSelectedTask().getParentTask();
        if(parent != null){
          //getTreeModel().removeNodeFromParent(getSelectedTask());
          parent.removeSubTask(getSelectedTask());
          getTreeModel().reload();
        }
        goToTask(parent);
      }
    }catch(TaskException e){
      logger.error("could not remove activity", e);
    }
  }

  public String getName() {
    return "Remove activity";
  }
  public boolean isEnabled() {
    if(getSelectedTask() == null) return false;
    if(getSelectedTask().getAllPeriods().size() > 0) return false;
    return true;
  }

}
