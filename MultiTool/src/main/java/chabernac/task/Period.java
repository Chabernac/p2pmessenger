
package chabernac.task;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

import chabernac.event.ApplicationEventDispatcher;
import chabernac.task.event.PeriodChangedEvent;

public class Period implements Comparable, Serializable{
  private static final long serialVersionUID = -5802645805801029937L;
  private long startTime = -1;
  private long endTime = -1;
  private Task task = null;
  private boolean isManuallyModified = false;
  
  public Period(){
    startTime = System.currentTimeMillis();
  }
  
  public long getEndTime() {
    return endTime;
  }
  public void setEndTime(long endTime) {
    this.endTime = endTime;
    ApplicationEventDispatcher.fireEvent( new PeriodChangedEvent(this) );
  }
  public long getStartTime() {
    return startTime;
  }
  public void setStartTime(long startTime) {
    this.startTime = startTime;
    ApplicationEventDispatcher.fireEvent( new PeriodChangedEvent(this) );
  }
  public Task getTask() {
    return task;
  }
  public void setTask(Task task) {
    this.task = task;
    ApplicationEventDispatcher.fireEvent( new PeriodChangedEvent(this) );
  }
  public long getTime(){
    long theEndTime = endTime;
    if(theEndTime == -1) theEndTime = System.currentTimeMillis();
    return theEndTime - startTime;
  }

  public int compareTo(Object o) {
    if(!(o instanceof Period)) return 0;
    Period thePeriod = (Period)o;
    //return (int)(getStartTime() - thePeriod.getStartTime());
    long time1 = getStartTime();
    long time2 = thePeriod.getStartTime();
    if(time1 < time2) return -1;
    if(time1 > time2) return 1;
    return 0;
  }
  
  public String toString(){
    return task.getFullName() + " - " + getStartTime() + " --> " + getEndTime() + "\n";
  }

  public boolean isManuallyModified() {
    return isManuallyModified;
  }

  public void setManuallyModified( boolean anIsManuallyModified ) {
    isManuallyModified = anIsManuallyModified;
  }

  public boolean spansMidnigth() {
    if(getEndTime() == -1) return false;
    if(getStartTime() == -1) return false;
    GregorianCalendar theEndTime = new GregorianCalendar();
    theEndTime.setTimeInMillis( getEndTime() );
    GregorianCalendar theStartTime = new GregorianCalendar();
    theStartTime.setTimeInMillis( getStartTime() );
    return theEndTime.get( Calendar.DAY_OF_YEAR ) != theStartTime.get(Calendar.DAY_OF_YEAR);
  }
}
