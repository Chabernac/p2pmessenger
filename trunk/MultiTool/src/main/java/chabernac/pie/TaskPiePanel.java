package chabernac.pie;

import java.awt.event.MouseEvent;

import chabernac.event.ApplicationEventDispatcher;
import chabernac.event.Event;
import chabernac.event.iEventListener;
import chabernac.task.Task;
import chabernac.task.TaskTools;
import chabernac.task.event.TaskSelectedEvent;

public class TaskPiePanel extends PiePanel implements iPieListener, iEventListener {
  private Task myRoot = null;
  private Pie myPie = null;
  
  public TaskPiePanel(Task aRootTask){
    super();
    ApplicationEventDispatcher.addListener(this, TaskSelectedEvent.class);
    myPie = new Pie();
    setPie(myPie);
    setPieListener(this);
    setRoot(aRootTask);
    setFocusable(true);
  }
  
  public void setRoot(Task aTask){
    myRoot = aTask;
    fillPie();
  }
  
  private void fillPie(){
    myPie.clear();
    if(myRoot != null){
      myPie.setName(myRoot.getFullName() + " (" + TaskTools.formatTimeInManDays(myRoot.getTimeWorked()) + ")" );
      for(int i=0;i<myRoot.getChildCount();i++){
        myPie.addPiece(new TaskPiece((Task)myRoot.getChildAt(i)));
      }
    }
    repaint();
  }

  public void pieceSelectedEvent(Piece aPiece, MouseEvent anEvent) {
    if(anEvent.getButton() == MouseEvent.BUTTON1){
      Task theTask = ((TaskPiece)aPiece).getTask();
      if(theTask.getChildCount() > 0){
        setRoot( theTask );
      }
    } else {
      if(myRoot != null && myRoot.getParentTask() != null){
        setRoot(myRoot.getParentTask());
      }  
    }
  }

  public void eventFired(Event anEvent) {
    setRoot (((TaskSelectedEvent)anEvent).getTask());
  }

}
