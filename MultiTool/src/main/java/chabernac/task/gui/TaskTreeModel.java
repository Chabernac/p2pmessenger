package chabernac.task.gui;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import chabernac.application.ApplicationRefBase;
import chabernac.task.Task;

public class TaskTreeModel extends DefaultTreeModel {
  private boolean showFinishedTasks = true;
  
  public TaskTreeModel() {
    super((Task)ApplicationRefBase.getInstance().get(ApplicationRefBase.ROOTTASK));
  }
  
  public int getChildCount(Object aParent){
    Task theTask = (Task)aParent;
    if(showFinishedTasks) return theTask.getChildCount();
    return getUnfinishedChildren(theTask);
  }
  
  public Object getChild(Object aParent, int index){
    Task theTask = (Task)aParent;
    TreeNode theChild = null;
    if(showFinishedTasks) {
       theChild = theTask.getChildAt(index);
    } else {
      theChild = getUnfinishedChildAt((Task)aParent, index); 
    }
    return theChild; 
  }
  
  public void setShowFinished(boolean showFinished){
    showFinishedTasks = showFinished;
  }
  
  public static int getUnfinishedChildren(Task aTask){
    int counter = 0;
    for(int i=0;i<aTask.getChildCount();i++){
      if(!((Task)aTask.getChildAt(i)).isCompleted()) counter++;
    }
    return counter;
  }
  
  public Task getUnfinishedChildAt(Task aTask, int index){
    int counter = 0;
    for(int i=0;i<aTask.getChildCount();i++){
      if(!((Task)aTask.getChildAt(i)).isCompleted()){
        if(counter == index) return (Task)aTask.getChildAt(i); 
        counter++;
      }
    }
    return null;
  }

  public boolean isShowFinishedVisible() {
    return showFinishedTasks;
  }
}
