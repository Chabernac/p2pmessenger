
package chabernac.test;

import java.util.GregorianCalendar;

import chabernac.record.R0004;
import chabernac.record.Record;
import chabernac.record.RecordLoader;
import chabernac.task.Period;
import chabernac.task.Task;
import chabernac.task.TaskException;
import chabernac.task.gui.TaskDialog;

public class TestTask {
  
  public static void main(String args[]){
	Record theR0004 =new R0004();
	System.out.println("length: " + theR0004.getLength());
	byte[] bytes = theR0004.getContent();
	theR0004  = RecordLoader.loadRecord(bytes);
	System.out.println("length: " + theR0004.getLength());
	
	if(true) return;
	  
    System.out.println(System.getProperty("user.name"));
    
    try{
    Task theRootTask = new Task(Task.GENERAL_TASK, "Root");
    Task theTask1 = new Task(Task.GENERAL_TASK, "Problemnr1");
    Task theTask2 = new Task(Task.GENERAL_TASK, "Problemnr2");
    theTask1.addSubTask(theTask2);
    theRootTask.addSubTask(theTask1);
    
    TaskDialog theDialog = new TaskDialog(theRootTask);
    theDialog.setVisible(true);
    
    
      //TaskContainer theContainer = new TaskContainer();
      Task theTask = new Task(Task.GENERAL_TASK, "TaskSheduler");
      theTask.setPriority(Task.LOW);
      Task theSubTask = new Task(Task.GENERAL_TASK, "coding");
      theSubTask.setPlannedTime(5);
      theTask.addSubTask(theSubTask);
      //theContainer.addTask(theTask);
      theSubTask.start();
      try{
        Thread.sleep(2000);
      }catch(Exception e){}
      theSubTask.stop();
      Period thePeriod = new Period();
      thePeriod.setStartTime(new GregorianCalendar(2005,7,01, 11, 0, 0).getTimeInMillis());
      thePeriod.setEndTime(new GregorianCalendar(2005,7,01, 13, 0, 0).getTimeInMillis());
      theSubTask.addPeriod(thePeriod);
      
      theTask = new Task(Task.GENERAL_TASK, "Verlof");
      thePeriod = new Period();
      thePeriod.setStartTime(new GregorianCalendar(2005,7,01, 9, 0, 0).getTimeInMillis());
      thePeriod.setEndTime(new GregorianCalendar(2005,7,05, 13, 0, 0).getTimeInMillis());
      theTask.addPeriod(thePeriod);
      //theContainer.addTask(theTask);
      
      //System.out.println(theContainer.toString());
      
      //TaskTools.makeCSV(theContainer,new GregorianCalendar(2005,7,01), new GregorianCalendar(2005,7,05), new File("c:\\temp\\ip\\ip.csv"));
      
    }catch(TaskException e){
      e.printStackTrace();
    }
  }

}

