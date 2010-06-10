
package chabernac.task.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import chabernac.application.ApplicationRefBase;
import chabernac.command.AbstractCommand;
import chabernac.gui.CommandButton;
import chabernac.task.Task;


public class TaskDialog extends JDialog{
  private boolean ok = false;
  private TaskPanel myTaskPanel = null;
  private Task myTask = null;
  
  public TaskDialog(Task aTask){
    super((JFrame)ApplicationRefBase.getObject(ApplicationRefBase.MAINFRAME), true);
    
    myTask = aTask;
    buildGUI(aTask);
  }
  
  private void buildGUI(Task aTask){
    setTitle( "Modify task '" + aTask.getFullName()  + "'");
    getContentPane().setLayout(new BorderLayout());
    myTaskPanel = new TaskPanel(aTask);
    getContentPane().add(myTaskPanel, BorderLayout.CENTER);
    JPanel theButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    getContentPane().add(theButtonPanel, BorderLayout.SOUTH);
    
    theButtonPanel.add(new CommandButton(new OKCommand()));
    theButtonPanel.add(new CommandButton(new CancelCommand()));
    theButtonPanel.add(new CommandButton(new ParentCommand()));
    
    setSize(new Dimension(450, 250));
    Point theLocation = ((JFrame)ApplicationRefBase.getObject(ApplicationRefBase.MAINFRAME)).getLocation();
    setLocation(theLocation.x + 10, theLocation.y + 10);
  }
  
  public boolean showTaskDialog(){
   setVisible(true);
   return ok;
  }
  
  private class ParentCommand extends AbstractCommand{
    public void execute() {
      new TaskDialog(myTask.getParentTask()).showTaskDialog();
      myTaskPanel.load();
    }

    public String getName() {
      return "Parent";
    }
    public boolean isEnabled() {
      return myTask.getParent() != null;
    }
  }
  
  private class OKCommand extends AbstractCommand{
    public void execute() {
      myTaskPanel.save();
      ok = true;
      dispose();
    }

    public String getName() {
      return "Ok";
    }
    public boolean isEnabled() {
      return true;
    }
  }
  
  private class CancelCommand extends AbstractCommand{
    public void execute() {
      ok = false;
      dispose();
    }

    public String getName() {
      return "Cancel";
    }
    public boolean isEnabled() {
      return true;
    }
  }
}
