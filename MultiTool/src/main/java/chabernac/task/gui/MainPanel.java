

package chabernac.task.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import chabernac.application.ApplicationRefBase;
import chabernac.event.ApplicationEventDispatcher;
import chabernac.gui.CommandButton;
import chabernac.task.Task;
import chabernac.task.command.CommandFactory;
import chabernac.task.command.DefaultActivityCommand;
import chabernac.task.event.TaskSelectedEvent;

public class MainPanel extends JPanel{
  private TaskTreePanel myTree = null;
  private TaskDetailPanel myDetail  = null;
  private DefaultActivityCommand myDefaultTaskCommand = null;
  
  public MainPanel(){
    buildGUI();
    addListeners();
  }
  
  private void addListeners(){
    myTree.getTree().getModel().addTreeModelListener(new MyTreeModelListener());
    myTree.getTree().addTreeSelectionListener(new MyTreeSelectionListener());
  }
  
  private void buildGUI(){
    setLayout(new BorderLayout());
    myTree = new TaskTreePanel();
    add(new JScrollPane(myTree), BorderLayout.CENTER);
    JPanel theButtonBorderPanel = new JPanel(new BorderLayout());
    JPanel theButtonPanel = new JPanel(new GridLayout(-1,1));
    theButtonBorderPanel.add(theButtonPanel,BorderLayout.NORTH);
    add(theButtonBorderPanel, BorderLayout.EAST);
    addButtons(theButtonPanel);
    myDetail = new TaskDetailPanel();
    add(myDetail, BorderLayout.SOUTH);
  }
  
  public void addButtons(JPanel aPanel){
    myDefaultTaskCommand = (DefaultActivityCommand)CommandFactory.getCommand("default");
    ApplicationRefBase.getInstance().put(ApplicationRefBase.DEFAULTTASKCOMMAND, myDefaultTaskCommand);
    aPanel.add(new CommandButton(CommandFactory.getCommand("create")));
    aPanel.add(new CommandButton(CommandFactory.getCommand("modify")));
    aPanel.add(new CommandButton(CommandFactory.getCommand("remove")));
    aPanel.add(new CommandButton(CommandFactory.getCommand("startstop")));
    aPanel.add(new CommandButton(CommandFactory.getCommand("completed")));
    //aPanel.add(new CommandButton(new ExportCSVActivityCommand(aTree)));
    aPanel.add(new CommandButton(CommandFactory.getCommand("showfinished")));
    aPanel.add(new CommandButton(CommandFactory.getCommand("parent")));
    aPanel.add(new CommandButton(CommandFactory.getCommand("current")));
    aPanel.add(new CommandButton(CommandFactory.getCommand("mostimportant")));
    aPanel.add(new CommandButton(CommandFactory.getCommand("todo")));
    aPanel.add(new CommandButton(CommandFactory.getCommand("search")));
  }
  
  private class MyTreeModelListener implements TreeModelListener{

    public void treeNodesChanged(TreeModelEvent e) {
      TreePath path = e.getTreePath();
      if(path != null){
        Task theTask = (Task)path.getLastPathComponent();
        myDetail.setTask(theTask);
      }
    }

    public void treeNodesInserted(TreeModelEvent e) {
    }

    public void treeNodesRemoved(TreeModelEvent e) {
    }

    public void treeStructureChanged(TreeModelEvent e) {
      
    }
  }
  
  private class MyTreeSelectionListener implements TreeSelectionListener{
    public void valueChanged(TreeSelectionEvent e) {
      if(myDefaultTaskCommand.getSelectedTask() != null){
        ApplicationEventDispatcher.fireEvent(new TaskSelectedEvent(myDefaultTaskCommand.getSelectedTask()));
        myDetail.setTask(myDefaultTaskCommand.getSelectedTask());
      }
    }
  }
  
}
