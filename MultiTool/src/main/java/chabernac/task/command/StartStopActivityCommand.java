package chabernac.task.command;

import chabernac.task.Task;
import chabernac.task.TaskTools;

public class StartStopActivityCommand extends ActivityCommand {
  
  StartStopActivityCommand(){
    super();
  }
  
  public void executeCommand() {
    Task theSelectedTask = getSelectedTask();
    Task theRunningTask = TaskTools.getRunningTask();
    
    if(theSelectedTask == theRunningTask){
    	TaskTools.stopRunningTask();
    } else {
    	TaskTools.startTask(theSelectedTask);
    }
  }

  public String getName() {
    Task theSelectedTask = getSelectedTask();
    if(theSelectedTask == null) return "Start activity";
    if(theSelectedTask.isRunning()) return "Stop activity";
    return "Start activity";
  }

  public boolean isEnabled() {
    Task theSelectedTask = getSelectedTask();
    if(theSelectedTask == null) return false;
    if(theSelectedTask.getChildCount() > 0) return false;
    return true;
  }

}
