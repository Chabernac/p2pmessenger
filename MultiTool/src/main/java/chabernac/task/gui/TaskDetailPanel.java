package chabernac.task.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import chabernac.task.Task;
import chabernac.task.TaskTools;

public class TaskDetailPanel extends JPanel implements Runnable{
  private static Logger logger = Logger.getLogger(TaskDetailPanel.class);
  private JLabel myName  = null;
  private JLabel myCompleted  = null;
  private JLabel myTimeWorked = null;
  private JLabel myTimeRemaining = null;
  private JLabel myProblemNr = null;
  
  private Task myCurrentTask = null;
  private NumberFormat myNumberFormat = null;
      
  public TaskDetailPanel(){
    init();
    buildGUI();
    startThread();
  }
  
  private void startThread(){
    new Thread(this).start();
  }
  
  private void init(){
    myNumberFormat = NumberFormat.getInstance(Locale.getDefault());
    myNumberFormat.setMaximumFractionDigits(3);
    myNumberFormat.setMinimumFractionDigits(3);
    
    myName = buildGrayLabel("");
    myTimeWorked = buildGrayLabel("");
    myTimeRemaining = buildGrayLabel("");
    myCompleted = buildGrayLabel("");
    myProblemNr = buildGrayLabel("");
  }
  
  private void buildGUI(){
    setLayout(new BorderLayout());
    JPanel thePanel = new JPanel(new BorderLayout());
    add(thePanel, BorderLayout.NORTH);
    JPanel theLabelPanel = new JPanel(new GridLayout(-1,1));
    JPanel theFieldPanel = new JPanel(new GridLayout(-1,1));
    thePanel.add(theLabelPanel, BorderLayout.WEST);
    thePanel.add(theFieldPanel, BorderLayout.CENTER);
    
    theLabelPanel.add(new JLabel("Name: "));
    theLabelPanel.add(new JLabel("Completed: "));
    theLabelPanel.add(new JLabel("Time worked on task: "));
    theLabelPanel.add(new JLabel("Time remaining for task: "));
    theLabelPanel.add(new JLabel("Problem nr.: "));
    
    theFieldPanel.add(myName);
    theFieldPanel.add(myCompleted);
    theFieldPanel.add(myTimeWorked);
    theFieldPanel.add(myTimeRemaining);
    theFieldPanel.add(myProblemNr);
  }
  
  private JLabel buildGrayLabel(String aText){
    JLabel theLabel = new JLabel(aText);
    theLabel.setForeground(Color.gray);
    return theLabel;
  }
  
  public void setTask(Task aTask){
    myCurrentTask = aTask;
    load();
  }
  
  public void load(){
    if(myCurrentTask == null) return;
    myName.setText(myCurrentTask.getFullName());
    myTimeRemaining.setText(TaskTools.formatTimeInHours(myCurrentTask.getRemainingTime()));
    myTimeWorked.setText(TaskTools.formatTimeInHours(myCurrentTask.getTimeWorked()) + " (" + TaskTools.formatTimeInManDays(myCurrentTask.getTimeWorked()) + ")");
    myCompleted.setText(Boolean.toString(myCurrentTask.isCompleted()));
    myProblemNr.setText(Integer.toString(myCurrentTask.getNotesProblemNr()));
  }
  
  public void run(){
    try{
      while(true){
         Thread.sleep(1000);
         load();
      }
    }catch(Exception e){
      logger.error("Could not sleep", e);
    }
  }

}
