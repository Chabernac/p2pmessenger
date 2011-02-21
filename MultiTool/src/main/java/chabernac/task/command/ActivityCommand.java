package chabernac.task.command;

import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import chabernac.application.ApplicationRefBase;
import chabernac.command.AbstractCommand;
import chabernac.task.Task;
import chabernac.task.TaskTools;
import chabernac.task.gui.TaskTreeModel;

public abstract class ActivityCommand extends AbstractCommand {
  protected JTree myTree = null;
  private Toolkit toolkit = Toolkit.getDefaultToolkit();

  public ActivityCommand(){
    myTree = (JTree)ApplicationRefBase.getObject(ApplicationRefBase.TREE);
    myTree.getModel().addTreeModelListener(new MyTreeModelListener());
    myTree.addTreeSelectionListener(new MyTreeSelectionListener());
  }
  
  public TaskTreeModel getTreeModel(){
    return (TaskTreeModel)myTree.getModel();
  }
  
  public Task getSelectedTask(){
    TreePath thePath = myTree.getSelectionPath();
    if(thePath == null) {
      toolkit.beep();
      return null;
    }
    return (Task)myTree.getSelectionPath().getLastPathComponent();
  }
  
  public void update(){
    getTreeModel().reload();
  }
  
  public Task getRootTask(){
    return (Task)getTreeModel().getRoot();
  }
  
  public Task getRunningTask(){
    return getRootTask().getRunningTask();
  }
  
  public void execute(){
    executeCommand();
    notifyObs();
  }
  
  public void goToTask(Task aTask){
    TaskTools.selectTask( aTask );
  }
  
  protected abstract void executeCommand();
  
  private class MyTreeModelListener implements TreeModelListener{

    public void treeNodesChanged(TreeModelEvent e) {
      notifyObs();
    }
    public void treeNodesInserted(TreeModelEvent e) {
      notifyObs();
    }
    public void treeNodesRemoved(TreeModelEvent e) {
      notifyObs();
    }
    public void treeStructureChanged(TreeModelEvent e) {
      notifyObs();
    }
  }
  
  private class MyTreeSelectionListener implements TreeSelectionListener{
    public void valueChanged(TreeSelectionEvent e) {
      notifyObs();
    }
  }
}
