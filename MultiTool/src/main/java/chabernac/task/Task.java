
package chabernac.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;

import chabernac.event.ApplicationEventDispatcher;
import chabernac.object.Iterable;
import chabernac.task.event.TaskStartedEvent;
import chabernac.util.TreeIterator;

public class Task extends DefaultMutableTreeNode implements Comparable, Iterable{
  private static final long serialVersionUID = -6268742762084778954L;
  private static Logger logger = Logger.getLogger(Task.class);
  
  private ArrayList periods = new ArrayList();
  private String name = "";
  private int notesProblemNr = 0;
  private String testTrackerNr = "";
  private int type = -1;
  private int importance = 2;
  private long dueTime = -1;
  private int plannedTime = 0; //hours
  private boolean completed = false;
  private Period currentPeriod = null;
  
  public int getAugeoPolicy() {
    return augeoPolicy;
  }
  
  public int getInheritedAugeoPolicy(){
    if(augeoPolicy == AugeoPolicy.PARENT_AUGEO_POLICY){
      return getParentTask().getInheritedAugeoPolicy();
    }
    return augeoPolicy;
  }

  public void setAugeoPolicy( int anAugeoPolicy ) {
    augeoPolicy = anAugeoPolicy;
  }
  
  public void setAugeoPolicyForChildren( int anAugeoPolicy ) {
    for(int i=0;i<getChildCount();i++){
      Task theTask = (Task)getChildAt( i );
      theTask.setAugeoPolicy( anAugeoPolicy );
      theTask.setAugeoPolicyForChildren( anAugeoPolicy );
    }
  }

  private String description = "";
  private String augeoCode = "";
  private int augeoPolicy = AugeoPolicy.BOOK_ON_OWN_AUGEO_CODE;
    
  public static final int GENERAL_TASK = 1;
  public static final int PROJECT = 1;
  public static final int QAA = 2;
  public static final int PROBLEM = 3;
  
  public static final int LOW = 1;
  public static final int NORMAL = 2;
  public static final int HIGH = 3;
  
  public static class AugeoPolicy{
    //book the time of this item on this augeo code
    public static final int BOOK_ON_OWN_AUGEO_CODE = 0;
    //book the time of this item on the augeo code which has the most time
    public static final int BOOK_ON_MAX_AUGEO_CODE = 1;
    //book the time of this item on the augeo code which has the least time
    public static final int BOOK_ON_MIN_AUGEO_CODE = 2;
    //spread the time of this item over all augeo codes
    public static final int BOOK_ON_ALL_AUGEO_CODES = 3;
    //do not book the time of this item
    public static final int DO_NOT_BOOK = 4;
    //do not book the time of this item
    public static final int PARENT_AUGEO_POLICY = 5;
  }
  
  public Task(int aType, String aName){
    type = aType;
    name = aName;
  }
  
  public String getAugeoCode() {
    if(getAugeoPolicy() == AugeoPolicy.BOOK_ON_OWN_AUGEO_CODE){
      return augeoCode;
    }
    
    if(getAugeoPolicy() == AugeoPolicy.PARENT_AUGEO_POLICY && getParentTask() != null){
      return getParentTask().getAugeoCode();
    }
    
    return null;
  }

  public void setAugeoCode(String anAugeoCode) {
    if(getParentTask() == null || !anAugeoCode.equals(getParentTask().getAugeoCode())){
      //only set the augeo code if it differs from the parents augeo code.
      augeoCode = anAugeoCode;
    }
  }
  
  public boolean hasAugeoCode(){
    String theAugeoCode = getAugeoCode();
    return theAugeoCode != null && !"".equals(theAugeoCode);
  }

  public static int getQAA() {
    return QAA;
  }
  public String getName() {
    return name;
  }
  
  public String getFullNameBrackets(){
    String theName = getName();
    if(getParentTask() != null){
      theName += " [" + getParentTask().getFullName() + "]";
    }
    return theName;
  }
  
  public String getFullName(){
    String name = "";
    if(getParentTask() != null) {
      String theParentTaskName = getParentTask().getFullName();
      if(!theParentTaskName.equals("")) name += theParentTaskName + " - ";
    }
    else return "";
    name += getName();
    if(getNotesProblemNr() > 0) name += " (PR:" + getNotesProblemNr() + ") ";
    if(!"".equals(getTestTrackerNr())) name += "(TT:" + getTestTrackerNr() + ")";
    return name;
  }
  
  public Vector getSortedTasks(){
    Vector theTasks = new Vector();
    for(int i=0;i<getChildCount();i++){
      Task theTask = (Task)getChildAt(i);
      theTasks.add(theTask);
      theTasks.addAll(theTask.getSortedTasks());
    }
    Collections.sort(theTasks, new TaskComparator());
    return theTasks;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }
  public int getNotesProblemNr() {
    return notesProblemNr;
  }
  public void setNotesProblemNr(int notesProblemNr) {
    this.notesProblemNr = notesProblemNr;
  }
  public ArrayList getPeriods() {
    return periods;
  }
  public void setPeriods(ArrayList periods) {
    this.periods = periods;
  }
  public String getTestTrackerNr() {
    return testTrackerNr;
  }
  public void setTestTrackerNr(String testTrackerNr) {
    this.testTrackerNr = testTrackerNr;
  }
  public int getType() {
    return type;
  }
  public void setType(int type) {
    this.type = type;
  }
  public long getDueTime() {
    return dueTime;
  }
  public void setDueTime(long dueTime) {
    this.dueTime = dueTime;
  }
  public int getImportance() {
    return importance;
  }
  public void setPriority(int importance) {
    this.importance = importance;
  }
  public int getPlannedTime() {
    if(getChildCount() > 0){
      int time = 0;
      for(int i=0;i<getChildCount();i++){
        time += ((Task)getChildAt(i)).getPlannedTime();
      }
      return time;
    } else {
      return plannedTime;
    }
  }
  public void setPlannedTime(int plannedTime) throws TaskException{
    if(getChildCount() > 0) throw new TaskException("If a task has subtasks you must set the planned time on the subtasks");
    this.plannedTime = plannedTime;
  }
  public void addPeriod(Period aPeriod) throws TaskException{
    if(getChildCount() > 0) throw new TaskException("If a task has subtasks you can only report on subtasks");
    periods.add(aPeriod);
    aPeriod.setTask(this);
  }
  
  public void removePeriod(Period aPeriod){
    aPeriod.setTask(null);
    periods.remove(aPeriod);
    if(aPeriod == currentPeriod) currentPeriod = null;
  }
  
  public void addSubTask(Task aTask) throws TaskException{
    if(periods.size() > 0) throw new TaskException("Can not add subtasks to this task because time has already been reported on this task");
    add(aTask);
  }
  
  public void removeSubTask(Task aTask) throws TaskException{
    if(periods.size() > 0) throw new TaskException("Can not remove subtask because time has already been reported on this task");
    remove(aTask);
  }
  public boolean isCompleted(){
    return completed;
  }
  
  public void setCompleted(boolean completed){
    this.completed = completed;
  }
  
  /*
  public TaskContainer getSubTasks(){
    return subTasks;
  }
  */
  
  public void start() throws TaskException{
    if(getChildCount() > 0) throw new TaskException("If a task has subtasks you can only report on subtasks");
    if(currentPeriod == null || currentPeriod.getEndTime() < System.currentTimeMillis()){
      currentPeriod = new Period();
      currentPeriod.setTask(this);
      addPeriod(currentPeriod);
      ApplicationEventDispatcher.fireEvent( new TaskStartedEvent( this ) );
    }
  }
  
  public void stop() throws TaskException{
    if(currentPeriod == null && !setRunningPeriod()) {
      throw new TaskException("This task was not yet started");
    }
    currentPeriod.setEndTime(System.currentTimeMillis());
    //addPeriod(currentPeriod);
    currentPeriod = null;
  }
  
  public void abort(){
    currentPeriod = null;
  }
  
  /**
   * 
   * @return time worked on this task in hours 
   */
  public long getTimeWorked(){
    long time = 0;
    if(getChildCount() > 0){
      for(int i=0;i<getChildCount();i++){
        time += ((Task)getChildAt(i)).getTimeWorked();
      }
    } else {
      for(int i=0;i<periods.size();i++){
        time += ((Period)periods.get(i)).getTime();
      }
    }
    return time;
  }
  
  public float getTimeWorkedInHours(){
    return (float)getTimeWorked() / (1000 * 60 * 60);
  }
  
  public long getRemainingTime(){
    long time = (getPlannedTime() * 60 * 60 * 1000)  - getTimeWorked();
    if(time < 0) return 0;
    return time;
  }
  
  public float getRemainingTimeInHours(){
    return (float)getRemainingTime() / (1000 * 60 * 60);
  }
  
  public void setParentTask(Task aTask){
    setParent(aTask);
  }
  
  public Task getParentTask(){
    return (Task)getParent();
  }
  
  public ArrayList getAllPeriods(){
    return getPeriods(-1, -1);
  }
  
  public ArrayList getPeriods(long aStartTime, long anEndTime){
    ArrayList thePeriods = new ArrayList();
    getPeriods(thePeriods, aStartTime, anEndTime);
    Collections.sort(thePeriods);
    return thePeriods;
  }
  
  public void getPeriods(ArrayList container, long aStartTime, long anEndTime){
    if(getChildCount() > 0){
      for(int i=0;i<getChildCount();i++){
        ((Task)getChildAt(i)).getPeriods(container, aStartTime, anEndTime); 
      }
    } else {
      Period thePeriod = null;
      for(int i=0;i<periods.size();i++){
        thePeriod = (Period)periods.get(i);
        long theEndTime = thePeriod.getEndTime();
        if(theEndTime == -1) theEndTime = System.currentTimeMillis();
        if((anEndTime == -1 || thePeriod.getStartTime() < anEndTime) && (aStartTime == -1 || theEndTime > aStartTime) ){
          container.add(thePeriod);
        }
      }
    }
  }
  
  public long getTimeReportedBetween(long aStartTime, long anEndTime){
    long time = 0;
    ArrayList thePeriods = getPeriods(aStartTime, anEndTime);
    Period thePeriod = null;
    for(int i=0;i<thePeriods.size();i++){
      thePeriod = (Period)thePeriods.get(i);
      long theStartTime = thePeriod.getStartTime();
      long theEndTime = thePeriod.getEndTime();
      if(theStartTime < aStartTime) theStartTime = aStartTime;
      if(theEndTime == -1) theEndTime = System.currentTimeMillis();
      if(theEndTime > anEndTime) theEndTime = anEndTime;
      time += (theEndTime - theStartTime);
    }
    return time;
  }
  
  public float getTimeReportedBetweenInHours(long aStartTime, long anEndTime){
    return (float)getTimeReportedBetween(aStartTime, anEndTime) / (1000 * 60 * 60);
  }
  
  public ArrayList getTasks(long aStartTime, long anEndTime){
    ArrayList thePeriods = getPeriods(aStartTime, anEndTime);
    ArrayList tasks = new ArrayList();
    Task theTask = null;
    for(int i=0;i<thePeriods.size();i++){
      
        theTask = ((Period)thePeriods.get(i)).getTask();
        while(theTask != null){
          if(!tasks.contains(theTask)){
            tasks.add(theTask);
          }
          theTask = theTask.getParentTask();
        }
    }
    Collections.sort(tasks);
    return tasks;
  }
  
  //only get the leave tasks between selected period
  public ArrayList getLeaveTasks(long aStartTime, long anEndTime){
    ArrayList thePeriods = getPeriods(aStartTime, anEndTime);
    ArrayList tasks = new ArrayList();
    Task theTask = null;
    for(int i=0;i<thePeriods.size();i++){

      theTask = ((Period)thePeriods.get(i)).getTask();
      if(!tasks.contains(theTask)){
        tasks.add(theTask);
      }
    }
    Collections.sort(tasks);
    return tasks;
  }
  
  public boolean setRunningPeriod(){
    for(int i=0;i<periods.size();i++){
      if(((Period)periods.get(i)).getEndTime() == -1){
        currentPeriod = (Period)periods.get(i);
        return true;
      }
    }
    return false;
  }
  
  public Task getRunningTask(){
    ArrayList thePeriods = getAllPeriods();
    Period thePeriod = null;
    for(int i=0;i<thePeriods.size();i++){
      thePeriod = (Period)thePeriods.get(i);
      if(thePeriod.getEndTime() == -1) {
        return thePeriod.getTask();
      }
    }
    return null;
  }
  
  public boolean isRunning(){
    Period thePeriod = null;
    for(int i=0;i<periods.size();i++){
      thePeriod = (Period)periods.get(i);
      if(thePeriod.getEndTime() == -1) return true;
    }
    return false;
  }
  
  public String toString(){
      return getName();
    /*
    String theString = "";
    theString += "Task name: " + getFullName() + "\n";
    if(subTasks.size() > 0){
      theString += " This task has subtasks: " + "\n";
      for(int i=0;i<subTasks.size();i++){
        theString += subTasks.get(i).toString();
      }
    } else {
      theString += " Periods worked on task: " + "\n";
      for(int i=0;i<periods.size();i++){
        theString += periods.get(i).toString();
      }
    }
    return theString;
    */
  }

  public int compareTo(Object o) {
    return getFullName().compareTo(((Task)o).getFullName());
  }
  
  public int getSequenceIndicator(){
    if(isCompleted()) return 0;
    String theIndicator = null;
    long theRemainingTime = getDueTime() - System.currentTimeMillis();
    if(getDueTime() >  0 && theRemainingTime < getRemainingTime()) theIndicator = "1";
    else theIndicator = "0";
    
    theIndicator += importance;
    
    if(getDueTime() <= 0) theIndicator += "000";
    else if(theRemainingTime < getRemainingTime()) theIndicator += "100";
    else {
      theIndicator += "0";
      int theNumber = Math.round((100 * getRemainingTime()) / theRemainingTime);
      logger.debug("Time remaining to complete task: " + getName() + ": " + getRemainingTime());
      logger.debug("Time remaining until end date of task: " + getName() + ": " + theRemainingTime);
      logger.debug("Number: " + theNumber);
      String theNumberString = Integer.toString(theNumber);
      if(theNumberString.length() < 2) theNumberString = "0" + theNumberString;
      theIndicator += theNumberString;
    }
    return Integer.parseInt(theIndicator);
  }

	public Iterator iterator() {
		return new TreeIterator(this); 
	}

}
