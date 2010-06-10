package chabernac.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import chabernac.application.ApplicationRefBase;
import chabernac.task.gui.TaskDialog;
import chabernac.util.StatusDispatcher;
import chabernac.utils.IOTools;



public class TaskTools {
  public static final long MANDAY_MILLISECONDS = (long)(7.5 * 60 * 60 * 1000);
  
  private static Logger logger = Logger.getLogger(TaskTools.class);
  public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EE dd-MM-yyyy HH:mm:ss"); 
  
  public static void makeCSV(Task aRoot, File aCSVFile){
    ArrayList thePeriods = aRoot.getAllPeriods();
    if(thePeriods.size() > 0){
      GregorianCalendar startDate = new GregorianCalendar();
      startDate.setTime(new Date( ((Period)thePeriods.get(0)).getStartTime() ));
      GregorianCalendar endDate = new GregorianCalendar();
      endDate.setTime(new Date( ((Period)thePeriods.get(thePeriods.size() - 1)).getStartTime() ));
      makeCSV(aRoot, startDate, endDate, aCSVFile);
    }
  }
  
  public static long nextDay(long aDay){
    GregorianCalendar theDate = new GregorianCalendar();
    theDate.setTimeInMillis(aDay);
    GregorianCalendar theDate2 = new GregorianCalendar(theDate.get(Calendar.YEAR), theDate.get(Calendar.MONTH), theDate.get(Calendar.DATE));
    theDate2.add(Calendar.DATE, 1);
    return theDate2.getTimeInMillis();
  }
  
  public static void makeCSV(Task aRoot, Calendar aStartDate, Calendar anEndDate, File aCSVFile){
    GregorianCalendar startDate = new GregorianCalendar(aStartDate.get(Calendar.YEAR), aStartDate.get(Calendar.MONTH), aStartDate.get(Calendar.DATE));
    GregorianCalendar endDate = new GregorianCalendar(anEndDate.get(Calendar.YEAR), anEndDate.get(Calendar.MONTH), anEndDate.get(Calendar.DATE));
    endDate.add(Calendar.DATE, 1);
    long theStartTime = startDate.getTimeInMillis();
    long theEndTime = endDate.getTimeInMillis();
    ArrayList theTasks = aRoot.getTasks(theStartTime, theEndTime);
    
    SimpleDateFormat theDateFormat = new SimpleDateFormat("EE yyyy/MM/dd");
    NumberFormat theFormat = NumberFormat.getInstance(new Locale("nl","BE"));
    theFormat.setMaximumFractionDigits(2);
    theFormat.setMinimumFractionDigits(2);
    
    PrintWriter theWriter = null;
    try{
      theWriter = new PrintWriter(new FileOutputStream(aCSVFile));
      GregorianCalendar theCurrentDate = (GregorianCalendar)startDate.clone();
      theWriter.print("Task;Augeo code;");
      while(theCurrentDate.before(endDate)){
        theWriter.print(theDateFormat.format(theCurrentDate.getTime()) + ";");
        theCurrentDate.add(Calendar.DATE, 1);
      }
      theWriter.print("Total;Remaining;");
      GregorianCalendar theDayAfterCurrent = null;
      theWriter.println();
      Task theCurrentTask = null;
      for(int i=0;i<theTasks.size();i++){
        theCurrentTask = (Task)theTasks.get(i);
        if(theCurrentTask.getName().equals("\\")){
          theWriter.print("Root" + ";;");
        } else {
          String theAugeoCode = "";
          if(theCurrentTask.getChildCount() == 0){
            theAugeoCode = theCurrentTask.getAugeoCode();
            if(theAugeoCode == null){
              if(theCurrentTask.getInheritedAugeoPolicy() == Task.AugeoPolicy.BOOK_ON_ALL_AUGEO_CODES) theAugeoCode = "Spread over all augeo codes";
              if(theCurrentTask.getInheritedAugeoPolicy() == Task.AugeoPolicy.BOOK_ON_MAX_AUGEO_CODE) theAugeoCode = "Book on max augeo code";
              if(theCurrentTask.getInheritedAugeoPolicy() == Task.AugeoPolicy.BOOK_ON_MIN_AUGEO_CODE) theAugeoCode = "Book on min augeo code";
              if(theCurrentTask.getInheritedAugeoPolicy() == Task.AugeoPolicy.DO_NOT_BOOK) theAugeoCode = "No augeo booking";
            }
          }

          theWriter.print(theCurrentTask.getFullName() + ";" + theAugeoCode + ";");
        }
        theCurrentDate = (GregorianCalendar)startDate.clone();
        while(!theCurrentDate.equals(endDate)){
          theDayAfterCurrent = (GregorianCalendar)theCurrentDate.clone();
          theDayAfterCurrent.add(Calendar.DATE, 1);
          theWriter.print(theFormat.format( theCurrentTask.getTimeReportedBetweenInHours(theCurrentDate.getTimeInMillis(), theDayAfterCurrent.getTimeInMillis())) + ";" );
          theCurrentDate.add(Calendar.DATE, 1);
        }
        theWriter.print(theFormat.format( theCurrentTask.getTimeReportedBetweenInHours(theStartTime, theEndTime)) + ";" );
        theWriter.print(theFormat.format( theCurrentTask.getRemainingTimeInHours()) + ";");
        theWriter.println();
      }
      
      
    }catch(IOException e){
      logger.error("Could not open output csv file", e);
    }finally{
      if(theWriter != null){
        theWriter.flush();
        theWriter.close();
      }
    }
  }
  
  public static void makeAugeoCSV(Task aRoot, Calendar aStartDate, Calendar anEndDate, File aCSVFile){
    GregorianCalendar startDate = new GregorianCalendar(aStartDate.get(Calendar.YEAR), aStartDate.get(Calendar.MONTH), aStartDate.get(Calendar.DATE));
    GregorianCalendar endDate = new GregorianCalendar(anEndDate.get(Calendar.YEAR), anEndDate.get(Calendar.MONTH), anEndDate.get(Calendar.DATE));
    endDate.add(Calendar.DATE, 1);
    long theStartTime = startDate.getTimeInMillis();
    long theEndTime = endDate.getTimeInMillis();
    ArrayList theTasks = aRoot.getLeaveTasks(theStartTime, theEndTime);
    
    //make a list of augeo codes
    Set theAugeoCodes = new TreeSet();
    for(Iterator i=theTasks.iterator();i.hasNext();){
      Task theTask = (Task)i.next();
      
      if(!theTask.hasAugeoCode() && theTask.getInheritedAugeoPolicy() == Task.AugeoPolicy.BOOK_ON_OWN_AUGEO_CODE){
        //this task has no augeo code, ask it.
        int theResult = JOptionPane.showConfirmDialog( null, "The task: '" + theTask.getFullName() + "' has no augeo code, do you want to give one or modify the augeo policy?" );
        
        if(theResult == JOptionPane.YES_OPTION){
          TaskDialog theDialog = new TaskDialog(theTask);
          theDialog.showTaskDialog();
        } else if(theResult == JOptionPane.CANCEL_OPTION){
          //stop
          return;
        }
      }
      
      if(theTask.hasAugeoCode()){
        theAugeoCodes.add( theTask.getAugeoCode() );
      } 
    }
    
    Map theAugeoLines = new TreeMap();
    Map theTotals = new HashMap();
    
    for(Iterator i=theAugeoCodes.iterator();i.hasNext();){
      String theAugeoCode = (String)i.next();
      String theLine = theAugeoCode + ";";
      theAugeoLines.put(theAugeoCode, theLine);
      theTotals.put(theAugeoCode, new Double(0));
    }
    
    theAugeoLines.put( "Total", "Total;");
    theTotals.put( "Total", new Double(0));
    
    GregorianCalendar theCurrentDate = (GregorianCalendar)startDate.clone();
    GregorianCalendar theDayAfterCurrent = null;
    NumberFormat theFormat = NumberFormat.getInstance(new Locale("nl","BE"));
    theFormat.setMaximumFractionDigits(2);
    theFormat.setMinimumFractionDigits(2);
    
    
    
    while(!theCurrentDate.equals(endDate)){
      theDayAfterCurrent = (GregorianCalendar)theCurrentDate.clone();
      theDayAfterCurrent.add(Calendar.DATE, 1);
      
      Map theTimes = calculateTimesForAugeoCodesBetween(theTasks, theCurrentDate.getTimeInMillis(), theDayAfterCurrent.getTimeInMillis());
      
      for(Iterator i=theAugeoLines.keySet().iterator();i.hasNext();){
        String theAugeoCode = (String)i.next();
        String theLine = ((String)theAugeoLines.get(theAugeoCode));
        
        if(theTimes.containsKey( theAugeoCode )){
          double theValue = ((Double)theTimes.get(theAugeoCode)).doubleValue() ;
          theLine += theFormat.format( theValue ) + ";";
          double theTotal = ((Double)theTotals.get(theAugeoCode)).doubleValue();
          theTotal += theValue;
          theTotals.put( theAugeoCode, new Double(theTotal) );
        } else {
          theLine += ";";
        }
        theAugeoLines.put(theAugeoCode, theLine);
      }
      
      theCurrentDate.add(Calendar.DATE, 1);
    }
    
    
    
    SimpleDateFormat theDateFormat = new SimpleDateFormat("EE yyyy/MM/dd");
    
    theCurrentDate = (GregorianCalendar)startDate.clone();
    theDayAfterCurrent = null;
    
    PrintWriter theWriter = null;
    try{
      theWriter = new PrintWriter(new FileOutputStream(aCSVFile));
      
      theWriter.print("Augeo code;");
      while(theCurrentDate.before(endDate)){
        theWriter.print(theDateFormat.format(theCurrentDate.getTime()) + ";");
        theCurrentDate.add(Calendar.DATE, 1);
      }
      theWriter.print("Total");
      
      theWriter.println();
      
      for(Iterator i=theAugeoLines.keySet().iterator();i.hasNext();){
        String theAugeoCode = (String)i.next();
        theWriter.println((String)theAugeoLines.get(theAugeoCode) + theFormat.format( ((Double)theTotals.get(theAugeoCode)).doubleValue() ));
      }
      
      
    }catch(IOException e){
      logger.error("Could not open output csv file", e);
    }finally{
      if(theWriter != null){
        theWriter.flush();
        theWriter.close();
      }
    }
  }
  
  private static Map calculateTimesForAugeoCodesBetween(List aTaskList, long aStartTime, long anEndTime){
    Map theTimes = new HashMap();
    double theSpreadTime = 0;
    double theMaxTime = 0;
    double theMinTime = 0;

    for(Iterator i=aTaskList.iterator();i.hasNext();){
      Task theTask = (Task)i.next();
      
      double theTaskTime = theTask.getTimeReportedBetweenInHours( aStartTime, anEndTime );
      
      if(theTaskTime > 0){
        if(theTask.getInheritedAugeoPolicy() == Task.AugeoPolicy.BOOK_ON_OWN_AUGEO_CODE && theTask.hasAugeoCode()){
          if(!theTimes.containsKey( theTask.getAugeoCode() )){
            theTimes.put( theTask.getAugeoCode(), new Double(0) );
          }
          double theTime = ((Double)theTimes.get(theTask.getAugeoCode())).doubleValue();
          theTime += theTaskTime;
          theTimes.put( theTask.getAugeoCode(), new Double(theTime) );
        } else if(theTask.getInheritedAugeoPolicy() == Task.AugeoPolicy.BOOK_ON_MAX_AUGEO_CODE){
          theMaxTime += theTaskTime;
        } else if(theTask.getInheritedAugeoPolicy() == Task.AugeoPolicy.BOOK_ON_MIN_AUGEO_CODE){
          theMinTime += theTaskTime;
        } else if(theTask.getInheritedAugeoPolicy() == Task.AugeoPolicy.BOOK_ON_ALL_AUGEO_CODES){
          theSpreadTime += theTaskTime;
        }
      }
    }
  
    //now divide the spread, min and max time
    
    //first the spread time
    double theSpreadPart = theSpreadTime / (double)theTimes.size();
    
    double theMinAugeoCodeValue = -1;
    String theMinAugeoCode = null;
    double theMaxAugeoCodeValue = 0;
    String theMaxAugeoCode = null;
    
    for(Iterator i=theTimes.keySet().iterator();i.hasNext();){
      String theCode = (String)i.next();
      double theTime = ((Double)theTimes.get( theCode )).doubleValue();
      theTime += theSpreadPart;
      theTimes.put( theCode, new Double(theTime) );
      
      if(theTime > theMaxAugeoCodeValue){
        theMaxAugeoCodeValue = theTime;
        theMaxAugeoCode = theCode;
      }
      
      if(theMinAugeoCodeValue == -1 || theTime < theMinAugeoCodeValue){
        theMinAugeoCodeValue = theTime;
        theMinAugeoCode = theCode;
      }
    }
    
    theMinAugeoCodeValue += theMinTime;
    theMaxAugeoCodeValue += theMaxTime;
    
    if(theMinAugeoCode != null) theTimes.put( theMinAugeoCode, new Double(theMinAugeoCodeValue));
    if(theMaxAugeoCode != null) theTimes.put( theMaxAugeoCode, new Double(theMaxAugeoCodeValue));
    
    //calculate total
    double theTotal = 0;
    for(Iterator i=theTimes.values().iterator();i.hasNext();){
      theTotal += ((Double)i.next()).doubleValue();
    }
    theTimes.put("Total", new Double(theTotal));
    
    return theTimes;
  }
  
  public static Task loadTask(File aFile){
	  Object theObject = IOTools.loadObject(aFile);
	  if(theObject != null){
		  if(theObject instanceof Task) return (Task)theObject; 
		  if(theObject instanceof HashMap) return (Task)((HashMap)theObject).get(ApplicationRefBase.ROOTTASK);
	  }
	  return  new Task(Task.GENERAL_TASK, "/");
  }
  
  public static ArrayList loadToDo(File aFile){
	  Object theObject = IOTools.loadObject(aFile);
	  if(theObject != null){
		  if(theObject instanceof ArrayList) return (ArrayList)theObject; 
		  if(theObject instanceof HashMap) return (ArrayList)((HashMap)theObject).get(ApplicationRefBase.TODO);
	  }
	  return  new ArrayList();
  }
  
  public static String formatTimestamp(long aTime){
    if(aTime == -1) aTime = System.currentTimeMillis();
    return DATE_FORMAT.format(new Date(aTime));
  }
  
  public static long parseTimestamp(String aTime) throws ParseException{
    return DATE_FORMAT.parse(aTime).getTime();
  }
  
  public static String formatTimeInHours(long aTime){
    float hours = (float)aTime / (3600000);
    int theHour = (int)Math.floor(hours);
    float minutes = (hours - theHour) * 60;
    int theMinutes = (int)Math.floor(minutes);
    float seconds = (minutes - theMinutes) * 60;
    int theSeconds = (int)Math.floor(seconds);
    return theHour + "h " + theMinutes + "m " + theSeconds + "s"; 
  }
  
  public static String formatTimeInManDays(long aTime){
    NumberFormat theFormat = NumberFormat.getInstance();
    theFormat.setMaximumFractionDigits(2);
    theFormat.setMinimumFractionDigits(2);
    return theFormat.format((double)aTime / (double)MANDAY_MILLISECONDS) + " md";
  }
  
  public static int getAvailablePortAbove(int aSocket){
	  boolean found = false;
	  int theSocketNr = aSocket;
	  while(!found){
		  ServerSocket theSocket = null;
		  try{
			  theSocket = new ServerSocket(theSocketNr);
			  found = true;
			  return theSocketNr;
		  }catch(Exception e){
        logger.error("Port " + aSocket  + " in use, increasing port number", e);
			  theSocketNr++;
			  if(theSocketNr > aSocket + 100) return -1;
		  } finally {
			  if(theSocket != null && !theSocket.isClosed()){
				  try{
					  theSocket.close();
				  }catch(IOException e){
            logger.error("Could not close socket", e);
				  }
			  }
		  }
	  }
	  return -1;
  }
  
  public static Task getRootTask(){
	  return (Task)ApplicationRefBase.getObject(ApplicationRefBase.ROOTTASK);
  }
  
  public static Task getRunningTask(){
	  return getRootTask().getRunningTask();
  }
  
  public static ArrayList getToDoList(){
	  return (ArrayList)ApplicationRefBase.getObject(ApplicationRefBase.TODO);
  }
  
  public static void startTask(Task aTask){
  	  try{
        Task theRunningTask = getRunningTask();
        if(theRunningTask == aTask) {
          StatusDispatcher.showWarning("This task is already running");
        } else {
          if(theRunningTask != null) theRunningTask.stop();
          aTask.start();
        }
      }catch(TaskException e){
        logger.error("Could not start activity", e);
      }
  }
  
  public static void stopRunningTask(){
	  Task theRunningTask = getRunningTask();
	  try{
		  if(theRunningTask != null) theRunningTask.stop();
	  }catch(TaskException e){
      logger.error("Could not stop task",e);
	  }
  }
}
