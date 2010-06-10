package chabernac.task.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import chabernac.task.Period;
import chabernac.task.Task;

public class PeriodPaintPanel extends JPanel {
  private static Logger logger = Logger.getLogger(PeriodPaintPanel.class);
  private static final long HOUR = 60 * 60 * 1000;
  private static final long DAY = 24 * HOUR;
  
  
  private ArrayList myPeriods = null;
  private long myStartTime = -1;
  private long myEndTime = -1;
  private double timeScale = 0; //pixels / milliseconds
  private double taskScale = 0; //pixels / task
  private ArrayList myTasks = null;
  private int topoffset = 10;
  //private int yspacing
  private Task mySelectedTask = null;
  
  public PeriodPaintPanel(){
    addListeners();
  }
  
  private void addListeners(){
    addMouseListener(new MyMouseAdapter());
  }
  
  
  public void setPeriods(ArrayList periods){
    myPeriods = periods;
    prepare();
    repaint();
  }
  
  public void setSelectedTask(Task aTask){
    mySelectedTask = aTask;
  }
  
  public void paint(Graphics g){
    g.setColor(Color.white);
    g.fillRect(0,0,getWidth(), getHeight());
    //paintDays(g);
    if(myPeriods == null) return;
    for(int i=0;i<myPeriods.size();i++){
      paint((Period)myPeriods.get(i),g);
    }
  }
  
  private void paintDays(Graphics g){
    GregorianCalendar theDate = new GregorianCalendar();
    theDate.setTimeInMillis(myStartTime);
    GregorianCalendar theNewDate = new GregorianCalendar();
    theNewDate.clear();
    theNewDate.set(GregorianCalendar.DAY_OF_YEAR, theDate.get(GregorianCalendar.DAY_OF_YEAR));
    theNewDate.set(GregorianCalendar.YEAR, theDate.get(GregorianCalendar.YEAR));
    long theStartTime = theNewDate.getTimeInMillis();
    logger.debug("Starttime: " + new Date(theStartTime));
    
    while(theStartTime < myEndTime){
      g.setColor(Color.gray);
      drawTime(g, theStartTime);
      long nineOclock = theStartTime + 9 * HOUR;
      long fiveOclock = theStartTime + 17 * HOUR;
      g.setColor(Color.lightGray);
      drawTime(g, nineOclock);
      drawTime(g, fiveOclock);
      theStartTime += DAY;
    }
  }
  
  private void drawTime(Graphics g, long aTime){
    int x = getX(aTime);
    g.drawLine(x,0,x,getHeight()); 
  }
  
  
  private void prepare(){
    myStartTime = -1;
    myEndTime = -1;
    myTasks = new ArrayList();
    for(int i=0;i<myPeriods.size();i++){
      Period thePeriod = (Period)myPeriods.get(i);
      Task theTask = (Task)thePeriod.getTask();
      if(!myTasks.contains(theTask)) myTasks.add(theTask);
      if(myStartTime == -1 || thePeriod.getStartTime() < myStartTime) myStartTime = thePeriod.getStartTime();
      if(myEndTime == -1 || thePeriod.getEndTime() > myEndTime) myEndTime = thePeriod.getEndTime();
      if(thePeriod.getEndTime() == -1) myEndTime = System.currentTimeMillis();
    }
    
    timeScale =  (double)getWidth() / ((double)(myEndTime - myStartTime));
    taskScale = ((double)(getHeight() - topoffset)) / ((double)(myTasks.size() + 1));
  }
  
  private void paint(Period aPeriod, Graphics g){
    int x = getX(aPeriod.getStartTime());
    long theEndTime = aPeriod.getEndTime();
    if(theEndTime == -1) theEndTime = System.currentTimeMillis();
    int width = (int)((theEndTime - aPeriod.getStartTime()) * timeScale);
    if(width == 0) width = 1;
    int y = (int)(myTasks.indexOf(aPeriod.getTask()) * taskScale) + topoffset;
    //int height = (int)Math.floor(taskScale);
    int height = 6;
    g.setColor(getColor(myTasks.indexOf(aPeriod.getTask())));
    g.fillRect(x,y,width, height);
    g.fillRect(x,getHeight() - height, width, height);
    if(mySelectedTask != null && mySelectedTask == aPeriod.getTask()){
      g.setColor(Color.gray);
      g.drawString(aPeriod.getTask().getFullName(), 20, y);
      g.setColor(Color.black);
      g.drawRect(0,y,getWidth(), height);
    }
  }
  
  private int getX(long atime){
    return (int)((atime - myStartTime) * timeScale);
  }
  
  private Color getColor(int anr){
    int whichColor = anr  % 10;
    switch(whichColor){
      case 0: return new Color(200,0,0); 
      case 1: return new Color(0,200,0);
      case 2: return new Color(0,0,200);
      case 3: return new Color(200,0,200);
      case 4: return new Color(0,200,200);
      case 5: return new Color(100,100,200);
      case 6: return new Color(200,100,100); 
      case 7: return new Color(100,200,100);
      case 8: return new Color(100,100,200);
      case 9: return new Color(200,100,200);
    }
    return new Color(100,100,100);
    
//    int colorValue = anr * 256 / myTasks.size();
//    
//    int color = colorValue << 8 * whichColor;
//    
//    return new Color(color); 
    /*
    long theLong = (long)(Math.pow(256,3) * (double)anr / (double)myTasks.size());
    int red = (int)theLong % 256;
    int green = (int)(theLong / 256) % 256;
    int blue = (int)(theLong / (256 * 256)) % 256;
    return new Color(red, green, blue);
    */
  }
  
  public Task getTaskAt(int y){
    int which = (int)((y  - topoffset) / taskScale);
    return (Task)myTasks.get(which);
  }
  
  private class MyMouseAdapter extends MouseAdapter{
    public void mousePressed(MouseEvent evt){
      mySelectedTask = getTaskAt(evt.getY());
      repaint();
    }
  }
}
