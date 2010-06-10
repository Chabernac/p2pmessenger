package chabernac.pie;

import chabernac.task.Task;
import chabernac.task.TaskTools;

public class TaskPiece implements Piece {
  private Task myTask = null;
  
  public TaskPiece(Task aTask){
    myTask = aTask;
  }

  public String getName() {
    return myTask.getFullName() +   " (" + TaskTools.formatTimeInManDays(myTask.getTimeWorked()) + ")" ;
  }

  public double getWeight() {
    return myTask.getTimeWorked();
  }
  
  public Task getTask(){
    return myTask;
  }

}
