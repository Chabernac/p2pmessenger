package chabernac.task.gui;

import java.util.ArrayList;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import chabernac.application.ApplicationRefBase;
import chabernac.task.Period;
import chabernac.task.Task;
import chabernac.task.TaskTools;
import chabernac.task.command.DefaultActivityCommand;


public class PeriodTableModel implements TableModel {
  private long myStartTime = -1;
  private long myEndTime = -1;
  private ArrayList myPeriods = null;
  private long lastValueAtTime = 0;

  public int getColumnCount() {
    return 4;
  }

  public int getRowCount() {
    return getPeriods().size();
  }

  public boolean isCellEditable(int rowIndex, int columnIndex) {
    switch(columnIndex){
      case 0: return false;
      case 1: return true;
      case 2: return true;
      case 3: return false;
    }
    return false;
  }

  public Class getColumnClass(int columnIndex) {
    switch(columnIndex){
      case 0: return String.class;
      case 1: return String.class;
      case 2: return String.class;
      case 3: return String.class;
    }
    return null;
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    Period thePeriod = (Period)myPeriods.get(rowIndex);
    switch(columnIndex){
      case 0: return thePeriod.getTask().getFullName();
      case 1: return TaskTools.formatTimestamp(thePeriod.getStartTime());
      case 2: return TaskTools.formatTimestamp(thePeriod.getEndTime());
      case 3: return TaskTools.formatTimeInHours(thePeriod.getTime());
    }
    return null;
  }

  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    Period thePeriod = (Period)myPeriods.get(rowIndex);
    switch(columnIndex){
      case 2: thePeriod.setStartTime(((Long)aValue).longValue());
      case 3: thePeriod.setEndTime(((Long)aValue).longValue());
    }
    
  }

  public String getColumnName(int columnIndex) {
    switch(columnIndex){
      case 0: return "Activity";
      case 1: return "Start";
      case 2: return "End";
      case 3: return "Duration";
    }
    return null;
  }

  public void addTableModelListener(TableModelListener l) {
  }

  public void removeTableModelListener(TableModelListener l) {
  }
  
  public ArrayList getPeriods(){
    if(myPeriods == null || System.currentTimeMillis() - lastValueAtTime > 1000){
      DefaultActivityCommand theCommand = (DefaultActivityCommand)ApplicationRefBase.getInstance().get(ApplicationRefBase.DEFAULTTASKCOMMAND);
      Task theSelectedTask = theCommand.getSelectedTask();
      if(theSelectedTask == null) return new ArrayList();
      myPeriods = theCommand.getSelectedTask().getPeriods(myStartTime, myEndTime);
    }
    //Logger.log(this,"Periods: " + myPeriods.size());
    return myPeriods;

  }
  
  public void setEndTime(long anEndTime){
    myEndTime = anEndTime;
  }
  
  public long getEndTime(){
    return myEndTime;
  }
  
  public void setStartTime(long aStartTime){
    myStartTime = aStartTime;
  }
  
  public long getStartTime(){
    return myStartTime;
  }
} 

