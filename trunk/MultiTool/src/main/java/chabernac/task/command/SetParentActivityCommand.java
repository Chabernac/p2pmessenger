package chabernac.task.command;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.apache.log4j.Logger;

import chabernac.task.Task;
import chabernac.task.TaskException;


public class SetParentActivityCommand extends ActivityCommand {
  private static Logger logger = Logger.getLogger(SetParentActivityCommand.class);
  private Task mySelectedTask = null;
  
  SetParentActivityCommand(){
    super();
  }
  
  protected void executeCommand() {
    mySelectedTask = getSelectedTask();
    if(mySelectedTask == null) return;
    myTree.addMouseListener(new MyMouseListener());
  }

  public String getName() {
    return "Set new parent";

  }

  public boolean isEnabled() {
    if(getSelectedTask() == null) return false;
    return true;
  }
  
  public char getMnemonic(){
    return 'p';
  }
  
  private class MyMouseListener extends MouseAdapter{
    public void mousePressed(MouseEvent e) {
      if(getSelectedTask() != null){
        Task theRootTask = getSelectedTask();
        Task parent = (Task)mySelectedTask.getParent();
        try{
          parent.removeSubTask(mySelectedTask);
          theRootTask.addSubTask(mySelectedTask);
          //mySelectedTask.setParent(theRootTask);
          update();
          goToTask(mySelectedTask);
        }catch(TaskException f){
          try {
            parent.addSubTask(mySelectedTask);
          } catch (TaskException e1) {
            logger.error("Could not reassign parent", e1);
          }
        }finally{
          myTree.removeMouseListener(this);
        }
      }
    }
  }

}
