package chabernac.task.gui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;

import chabernac.application.ApplicationRefBase;
import chabernac.command.AbstractCommand;
import chabernac.event.ApplicationEventDispatcher;
import chabernac.event.Event;
import chabernac.event.iEventListener;
import chabernac.gui.CommandButton;
import chabernac.gui.GUIUtils;
import chabernac.pie.TaskPiePanel;
import chabernac.preference.ApplicationPreferences;
import chabernac.task.Period;
import chabernac.task.Task;
import chabernac.task.TaskTools;
import chabernac.task.command.DefaultActivityCommand;
import chabernac.task.event.ApplicationSaveEvent;
import chabernac.util.StatusDispatcher;

public class PeriodOverviewPanel extends JPanel implements iEventListener{
  private static Logger logger = Logger.getLogger(PeriodOverviewPanel.class);
  public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
  public static SimpleDateFormat DATE_FORMAT_2 = new SimpleDateFormat("dd-MM-yyyy");
  private JTabbedPane myPane = null;
  private JTable myTable = null;
  private JTextField myStartField = null;
  private JTextField myEndField = null;
  private PeriodTableModel myModel = null;
  private JLabel myTotal = null;
  private PeriodPaintPanel myPaintPanel = null;
  
  private JTextField myFilterStartField = null;
  private JTextField myFilterEndField = null;
  private FilterCommand myFilterCommand = null;
  
  public PeriodOverviewPanel(){
    init();
    buildGUI();
    loadPreferences();
    ApplicationEventDispatcher.addListener(this, ApplicationSaveEvent.class);
  }
  
  private void init(){
    myStartField = new JTextField(12);
    myEndField = new JTextField(12);
    myTotal = new JLabel();
    myFilterEndField = new JTextField(7);
    myFilterEndField.setToolTipText("dd-MM-yyyy");
    myFilterStartField = new JTextField(7);
    myFilterStartField.setToolTipText("dd-MM-yyyy");
    myModel = new PeriodTableModel();
    myTable = new JTable(myModel);
    myTable.setDefaultRenderer( String.class, new PeriodTableRenderer() );
    myTable.addMouseListener(new MyMouseAdapter());
    myFilterCommand = new FilterCommand();
    myPaintPanel = new PeriodPaintPanel();
    myPane = new JTabbedPane();
    
  }
  
  private void buildGUI(){
    setLayout(new BorderLayout());
    
    myPane.addTab("table", new JScrollPane(myTable));
    myPane.addTab("view", myPaintPanel);
    myPane.addTab("pie", new TaskPiePanel(null));
    
    add(myPane, BorderLayout.CENTER);
    
    JPanel theTotalPanel = new JPanel(new BorderLayout());
    theTotalPanel.add(new JLabel("Total time of selected periods: "), BorderLayout.WEST);
    theTotalPanel.add(myTotal, BorderLayout.CENTER);
    
    JPanel theSouthPanel = new JPanel(new BorderLayout());
    theSouthPanel.add(buildSouthPanel(), BorderLayout.CENTER);
    theSouthPanel.add(theTotalPanel, BorderLayout.SOUTH);
    
    add(theSouthPanel, BorderLayout.SOUTH);
  }
  
  private JPanel buildSouthPanel(){
    JPanel thePanel = new JPanel(new GridBagLayout());
    Insets theInsets = new Insets(0,3,0,3);
    GUIUtils.addComponent(thePanel, new JLabel("Start time: "), 0,0,0,0, GridBagConstraints.NONE,theInsets);
    GUIUtils.addComponent(thePanel, myStartField, 1, 0, 1, 0, GridBagConstraints.NONE,theInsets);
    GUIUtils.addComponent(thePanel, new JLabel("End time: "), 2,0,0,0, GridBagConstraints.NONE,theInsets);
    GUIUtils.addComponent(thePanel, myEndField, 3, 0, 1, 0, GridBagConstraints.NONE,new Insets(0,3,0,1));
    //GUIUtils.addComponent(thePanel, new JLabel(""), 4, 0, 0, 0, GridBagConstraints.NONE,new Insets(0,3,0,1));
    GUIUtils.addComponent(thePanel, new CommandButton(new StartCommand(), 70), 4, 0, 0, 0, GridBagConstraints.NONE,new Insets(0,1,0,1));
    GUIUtils.addComponent(thePanel, new CommandButton(new DeleteCommand(), 110), 5,0,0,0, GridBagConstraints.NONE,new Insets(0,1,0,1));
    GUIUtils.addComponent(thePanel, new CommandButton(new ModifyCommand(), 80), 6,0,0,0, GridBagConstraints.NONE,new Insets(0,1,0,0));
    
    GUIUtils.addComponent(thePanel, new JLabel("Start date: "), 0,1,0,0, GridBagConstraints.NONE,theInsets);
    GUIUtils.addComponent(thePanel, myFilterStartField, 1, 1, 1, 0, GridBagConstraints.NONE,theInsets);
    GUIUtils.addComponent(thePanel, new JLabel("End date: "), 2,1,0,0, GridBagConstraints.NONE,theInsets);
    GUIUtils.addComponent(thePanel, myFilterEndField, 3, 1, 1, 0, GridBagConstraints.NONE,new Insets(0,3,0,1));
    GUIUtils.addComponent(thePanel, new CommandButton(new TodayCommand(), 70), 4,1,0,0, GridBagConstraints.NONE,new Insets(0,1,0,1));
    GUIUtils.addComponent(thePanel, new CommandButton(myFilterCommand, 110), 5,1,0,0, GridBagConstraints.NONE,new Insets(0,1,0,1));
    GUIUtils.addComponent(thePanel, new CommandButton(new ExportCommand(false), 80), 6,1,0,0, GridBagConstraints.NONE,new Insets(0,1,0,0));
    GUIUtils.addComponent(thePanel, new CommandButton(new ExportCommand(true), 80), 7,1,0,0, GridBagConstraints.NONE,new Insets(0,1,0,0));
    
    return thePanel;
  }
  
  
  
  private void loadPreferences(){
    ApplicationPreferences thePreferences = ApplicationPreferences.getInstance();
    TableColumnModel theColumnModel = myTable.getColumnModel();
    for(int i=0;i<myModel.getColumnCount();i++){
      String theColumnName = myModel.getColumnName(i);
      int theWidth = Integer.parseInt(thePreferences.getProperty(theColumnName + ".width", "-1"));
      TableColumn theColumn = theColumnModel.getColumn(i);
      if(theWidth != -1) theColumn.setPreferredWidth(theWidth);
      theColumn.setHeaderValue(myModel.getColumnName(i));
    }
    myFilterStartField.setText(thePreferences.getProperty("filter.start"));
    myFilterEndField.setText(thePreferences.getProperty("filter.end"));
    myFilterCommand.execute();
  }
  
  private void savePreferences(){
    ApplicationPreferences thePreferences = ApplicationPreferences.getInstance();
    TableColumnModel theColumnModel = myTable.getColumnModel();
    for(int i=0;i<theColumnModel.getColumnCount();i++){
      TableColumn theColumn = theColumnModel.getColumn(i);
      thePreferences.setProperty(theColumn.getHeaderValue() + ".width", Integer.toString(theColumn.getWidth()));
    }
    thePreferences.setProperty("filter.start", myFilterStartField.getText());
    thePreferences.setProperty("filter.end", myFilterEndField.getText());
  }
  
  public void paint(Graphics g){
    super.paint(g);
    myTable.tableChanged(new TableModelEvent(myTable.getModel()));
    myPaintPanel.setPeriods(((PeriodTableModel)myTable.getModel()).getPeriods());
  }
  
  
  private class DeleteCommand extends TableCommand{
    public void execute() {
      if(myTable.getSelectedRows().length > 0){
        int[] theSelectedRows = myTable.getSelectedRows(); 
        Period[] theSelectedPeriods =new Period[theSelectedRows.length];
        for(int i=0;i<theSelectedRows.length;i++){
          theSelectedPeriods[i] = (Period)myModel.getPeriods().get(theSelectedRows[i]); 
        }
        for(int i=0;i<theSelectedPeriods.length;i++){
          Task theTask = theSelectedPeriods[i].getTask();
          theTask.removePeriod(theSelectedPeriods[i]);
        }
        myTable.tableChanged(new TableModelEvent(myTable.getModel()));
      } else {
        StatusDispatcher.showWarning("No row selected");
      }
    }

    public String getName() {
      return "Delete period";
    }

  }
  
  private class ModifyCommand extends TableCommand{

    public void execute() {
      int index = myTable.getSelectedRow();
      if(index != -1){
        Period thePeriod = (Period)myModel.getPeriods().get(index);
        try{
          thePeriod.setStartTime(DATE_FORMAT.parse(myStartField.getText()).getTime());
          if(myEndField.getText().equals("")) thePeriod.setEndTime(-1);
          else thePeriod.setEndTime(DATE_FORMAT.parse(myEndField.getText()).getTime());
          thePeriod.setManuallyModified( true );
          myTable.tableChanged(new TableModelEvent(myTable.getModel()));
          myTable.setRowSelectionInterval(index, index);
        }catch(ParseException e){
          StatusDispatcher.showWarning("Start and end time must be formatted as: dd-MM-yyyy HH:mm:ss");
          logger.error("Invalid date", e);
        }
      } else {
        StatusDispatcher.showWarning("No row selected");
      }
    }

    public String getName() {
      return "Modify";
    }

  }
  
  /*
  private class RefreshCommand extends AbstractCommand{
    public void execute(){
      myTable.tableChanged(new TableModelEvent(myTable.getModel()));
    }
    
    public String getName() {
      return "Refresh";
    }

    public boolean isEnabled() {
      return true;
    }
  }
  */
  
  private class FilterCommand extends AbstractCommand{

    public void execute() {
      try{
        if(myFilterStartField.getText().equals("")) myModel.setStartTime(-1);
        else myModel.setStartTime(DATE_FORMAT_2.parse(myFilterStartField.getText()).getTime());
        if(myFilterEndField.getText().equals("")) myModel.setEndTime(-1);
        else myModel.setEndTime(TaskTools.nextDay(DATE_FORMAT_2.parse(myFilterEndField.getText()).getTime()));
        myTable.tableChanged(new TableModelEvent(myTable.getModel()));
        myPaintPanel.setPeriods(((PeriodTableModel)myTable.getModel()).getPeriods());
      }catch(ParseException e){
        StatusDispatcher.showWarning("Filter dates must be formatted as dd-mm-yyy");
        logger.error("Could not apply filter", e);
      }
    }

    public String getName() {
      return "Apply filter";
    }

    public boolean isEnabled() {
      return true;
    }
    
  }
  
  private abstract class TableCommand extends AbstractCommand implements ListSelectionListener {
    
    public TableCommand(){
      myTable.getSelectionModel().addListSelectionListener(this);
      
    }
    
    public void valueChanged(ListSelectionEvent e){
      notifyObs();
    }

    public boolean isEnabled() {
      return myTable.getSelectedRow() != -1;
    }
  }
  
  private class ExportCommand extends AbstractCommand{
    private boolean isAugeoExport;
    
    public ExportCommand(boolean isAugeoExport){
      this.isAugeoExport = isAugeoExport;
    }
    
    public void execute(){
      myFilterCommand.execute();
      DefaultActivityCommand theCommand = (DefaultActivityCommand)ApplicationRefBase.getInstance().get(ApplicationRefBase.DEFAULTTASKCOMMAND);
      Task theSelectedTask = theCommand.getSelectedTask();
      GregorianCalendar theStartDate = new GregorianCalendar();
      if(myModel.getStartTime() != -1){
        theStartDate.setTimeInMillis(myModel.getStartTime());
      } else if(myModel.getPeriods().size() > 0){
        theStartDate.setTimeInMillis(((Period)myModel.getPeriods().get(0)).getStartTime());
      }
      GregorianCalendar theEndDate = new GregorianCalendar();
      if(myModel.getEndTime() != -1){
        theEndDate.setTimeInMillis(myModel.getEndTime());
        theEndDate.add(GregorianCalendar.DATE, -1);
      }
      File theFile = null;
      if(isAugeoExport){
        theFile = new File("augeo.csv");
        TaskTools.makeAugeoCSV(theSelectedTask, theStartDate, theEndDate, theFile);
      } else {
        theFile = new File("ip.csv");
        TaskTools.makeCSV(theSelectedTask, theStartDate, theEndDate, theFile);
      }
      try{
        Runtime.getRuntime().exec("cmd /c " + theFile.toString());
      }catch(IOException e){
        logger.error("Could not start csv");
      }
    }
    
    public String getName() {
      if(isAugeoExport){
        return "Augeo export";
      } else {
        return "Export";
      }
    }
    
    public boolean isEnabled() {
      return true;
    }
  }
  
  private class TodayCommand extends AbstractCommand{

    public void execute() {
      SimpleDateFormat theFormat = new SimpleDateFormat("dd-MM-yyyy");
      myFilterStartField.setText(theFormat.format(new Date()));
      myFilterEndField.setText("");
      myFilterCommand.execute();
    }

    public String getName() {
      return "Today";
    }

    public boolean isEnabled() {
      return true;
    }
  }
  
  private class StartCommand extends TableCommand{

    public void execute() {
      int index = myTable.getSelectedRow();
      if(index != -1){
        Period thePeriod = (Period)myModel.getPeriods().get(index);
        TaskTools.startTask(thePeriod.getTask());
        myTable.tableChanged(new TableModelEvent(myTable.getModel()));
      }
    }

    public String getName() {
      return "Start";
    }
  }
  
  private class MyMouseAdapter extends MouseAdapter{
    public void mouseClicked(MouseEvent e) {
      
      if(e.getClickCount() > 1){
        ArrayList thePeriods = myModel.getPeriods();
        Period thePeriod = (Period)thePeriods.get(myTable.getSelectedRow());
        Task theTask = thePeriod.getTask();
        for(int i=0;i<thePeriods.size();i++){
          if( ((Period)thePeriods.get(i)).getTask() == theTask){
            myTable.addRowSelectionInterval(i, i);
          }
        }
        calculateTotalTime();
      }
      
    }
    
    public void mousePressed(MouseEvent e) {
      if(myTable.getSelectedRow() != -1){
        Period thePeriod = (Period)myModel.getPeriods().get(myTable.getSelectedRow());
        myStartField.setText(DATE_FORMAT.format(new Date(thePeriod.getStartTime())));
        if(thePeriod.getEndTime() == -1) myEndField.setText("");
        else myEndField.setText(DATE_FORMAT.format(new Date(thePeriod.getEndTime())));
        myPaintPanel.setSelectedTask(thePeriod.getTask());
      }
    }
    
    public void mouseReleased(MouseEvent e) {
     calculateTotalTime();
    }
  }
  
  private void calculateTotalTime(){
    if(myTable.getSelectedRows().length > 0){
      long totalTime = 0;
      for(int i=0;i<myTable.getSelectedRows().length;i++){
        totalTime += ((Period)myModel.getPeriods().get(myTable.getSelectedRows()[i])).getTime();
      }
      myTotal.setText(TaskTools.formatTimeInHours(totalTime));
    }
  }

  public void eventFired(Event evt) {
    savePreferences();
  }

}
