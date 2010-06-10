package chabernac.task.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import chabernac.command.AbstractCommand;
import chabernac.gui.CommandButton;
import chabernac.gui.GUIUtils;
import chabernac.gui.NumericField;
import chabernac.task.Task;

public class TaskPanel extends JPanel{
  private static Logger logger = Logger.getLogger(TaskPanel.class);
  public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
  private Task myTask = null;
  
  private JTextField myName = null;
  private JTextField myAugeoCode = null;
  private NumericField myNotes = null;
  private JTextField myTT = null;
//  private JTextField myDueTime = null;
//  private JComboBox myPriority = null;
  private JComboBox myAugeoPolicy = null;
  private NumericField myPlannedTime = null;
  private JTextArea myDescription = null;
  
  
  public TaskPanel(Task aTask){
    myTask = aTask;
    init();
    buildGUI();
    load();
  }
  
  private void init(){
    myName = new JTextField();
    myNotes = new NumericField();
    myTT = new JTextField();
    myPlannedTime = new NumericField();
    myDescription = new JTextArea();
    myDescription.setLineWrap(true);
//    myDueTime = new JTextField();
//    myPriority = new JComboBox(new String[]{"Low","Medium","High"});
    myAugeoPolicy = new JComboBox(new String[]{"Book on own augeo code", "Book on max augeo code", "Book on min augeo code", "Spread over all augeo codes", "Do not book", "Parent augeo policy"});
    myAugeoCode = new JTextField();
  }
  
  private void buildGUI(){
    /*
    setLayout(new BorderLayout());
    JPanel thePanel = new JPanel(new BorderLayout());
    JPanel theLabelpanel = new JPanel(new GridLayout(-1,1));
    JPanel theFieldPanel = new JPanel(new GridLayout(-1,1));
    thePanel.add(theLabelpanel, BorderLayout.WEST);
    thePanel.add(theFieldPanel, BorderLayout.CENTER);
    add(thePanel, BorderLayout.NORTH);
    */
    setLayout(new GridBagLayout());
    Insets theInsets = new Insets(1,1,1,1);
    GUIUtils.addComponent(this, new JLabel("Name: ")                , 0,0,0,0, GridBagConstraints.NONE,theInsets);
    GUIUtils.addComponent(this, new JLabel("Augeo code: ")          , 0,1,0,0, GridBagConstraints.NONE,theInsets);
    GUIUtils.addComponent(this, new JLabel("Notes problemnr.: ")    , 0,2,0,0, GridBagConstraints.NONE,theInsets);
    GUIUtils.addComponent(this, new JLabel("Test tracker nr: ")     , 0,3,0,0, GridBagConstraints.NONE,theInsets);
    if(myTask.getChildCount() == 0) GUIUtils.addComponent(this, new JLabel("Planned time (hours): "), 0,4,0,0, GridBagConstraints.NONE,theInsets);
//    GUIUtils.addComponent(this, new JLabel("Due date: ")           , 0,5,0,0, GridBagConstraints.NONE,theInsets);
//    GUIUtils.addComponent(this, new JLabel("Priority: ")           , 0,6,0,0, GridBagConstraints.NONE,theInsets);
    GUIUtils.addComponent(this, new JLabel("Augeo policy: ")           , 0,5,0,0, GridBagConstraints.NONE,theInsets);
    GUIUtils.addComponent(this, new JLabel("Description: ")         , 0,6,0,0, GridBagConstraints.NONE,theInsets);
    
    GUIUtils.addComponent(this, myName        , 1,0,2,1,1,0, GridBagConstraints.HORIZONTAL,theInsets);
    GUIUtils.addComponent(this, myAugeoCode   , 1,1,2,1,1,0, GridBagConstraints.HORIZONTAL,theInsets);
    GUIUtils.addComponent(this, myNotes       , 1,2,2,1,1,0, GridBagConstraints.HORIZONTAL,theInsets);
    GUIUtils.addComponent(this, myTT          , 1,3,2,1,1,0, GridBagConstraints.HORIZONTAL,theInsets);
    if(myTask.getChildCount() == 0) GUIUtils.addComponent(this, myPlannedTime , 1,4,2,1,1,0, GridBagConstraints.HORIZONTAL,theInsets);
//    GUIUtils.addComponent(this, myDueTime     , 1,5,1,0, GridBagConstraints.HORIZONTAL,theInsets);
//    GUIUtils.addComponent(this, myPriority    , 1,6,1,0, GridBagConstraints.HORIZONTAL,theInsets);
    GUIUtils.addComponent(this, myAugeoPolicy    , 1,5,1,1,1,0, GridBagConstraints.HORIZONTAL,theInsets);
    GUIUtils.addComponent(this, new CommandButton(new InheritAugeoCodeCommand())   , 2,5,1,1,1,0, GridBagConstraints.HORIZONTAL,theInsets);
    GUIUtils.addComponent(this, new JScrollPane(myDescription) , 1,6,2,1,1,1, GridBagConstraints.BOTH,theInsets);
    
    
//    theLabelpanel.add(new JLabel("Name: "));
//    theLabelpanel.add(new JLabel("Notes problemnr.: "));
//    theLabelpanel.add(new JLabel("Test tracker nr: "));
//    theLabelpanel.add(new JLabel("Type: "));
//    theLabelpanel.add(new JLabel("Priority: "));
//    theLabelpanel.add(new JLabel("Due time: "));
//    theLabelpanel.add(new JLabel("Completed: "));
//    theLabelpanel.add(new JLabel("Planned time (hours): "));
//    theLabelpanel.add(new JLabel("Description: "));
    
//    theFieldPanel.add(myName);
//    theFieldPanel.add(myNotes);
//    theFieldPanel.add(myTT);
//    theFieldPanel.add(myPlannedTime);
//    theFieldPanel.add(myDescription);
  }
  
  public void load(){
    myName.setText(myTask.getName());
    myNotes.setText(Integer.toString(myTask.getNotesProblemNr()));
    myTT.setText(myTask.getTestTrackerNr());
    myPlannedTime.setText(Integer.toString(myTask.getPlannedTime()));
    myDescription.setText(myTask.getDescription());
//    myPriority.setSelectedIndex(myTask.getImportance() - 1);
//    myDueTime.setText(DATE_FORMAT.format(new Date(myTask.getDueTime())));
    myAugeoCode.setText(myTask.getAugeoCode());
    myAugeoPolicy.setSelectedIndex( myTask.getAugeoPolicy() );
  }
  
  public boolean save(){
    try{
      myTask.setDescription(myDescription.getText());
      myTask.setName(myName.getText());
      myTask.setNotesProblemNr(Integer.parseInt(myNotes.getText()));
      myTask.setTestTrackerNr(myTT.getText());
      if(myTask.getChildCount() == 0){
        myTask.setPlannedTime(Integer.parseInt(myPlannedTime.getText()));
      }
      myTask.setAugeoPolicy( myAugeoPolicy.getSelectedIndex() );
//      myTask.setPriority(myPriority.getSelectedIndex() + 1);
//      myTask.setDueTime(DATE_FORMAT.parse(myDueTime.getText()).getTime());
      myTask.setAugeoCode(myAugeoCode.getText());
      return true;
    }catch(Exception e){
      logger.error("Could not save", e);
      return false;
    }
  }
  
  private class InheritAugeoCodeCommand extends AbstractCommand{

    public String getName() {
      return "inherit to childs";
    }

    public boolean isEnabled() {
      return true;
    }

    public void execute() {
      myTask.setAugeoPolicyForChildren( Task.AugeoPolicy.PARENT_AUGEO_POLICY );      
    }
    
     
    
  }
  

}
