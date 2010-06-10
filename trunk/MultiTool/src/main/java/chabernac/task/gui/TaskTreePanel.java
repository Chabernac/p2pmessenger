package chabernac.task.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreeSelectionModel;

import chabernac.application.ApplicationRefBase;
import chabernac.event.ApplicationEventDispatcher;
import chabernac.event.Event;
import chabernac.event.iEventListener;
import chabernac.preference.ApplicationPreferences;
import chabernac.task.event.ApplicationSaveEvent;

public class TaskTreePanel extends JPanel implements iEventListener{
  private JTree myTree = null;
  private TaskTreeModel myModel = null;

  public TaskTreePanel(){
    initModel();
    buildGUI();
    loadPreferences();
    addListeners();
  }

  private void addListeners(){
    ApplicationEventDispatcher.addListener(this, ApplicationSaveEvent.class);
  }

  private void initModel(){
    myModel = new TaskTreeModel();
  }

  private void loadPreferences(){
    ApplicationPreferences thePreferences = ApplicationPreferences.getInstance();
    boolean showFinised = true;
    if(thePreferences.containsKey("tree.showfinished")) showFinised =  "Y".equalsIgnoreCase(thePreferences.getProperty("tree.showfinished"));
    myModel.setShowFinished(showFinised);
    myModel.reload();
  }

  private void savePreferences(){
    ApplicationPreferences thePreferences = ApplicationPreferences.getInstance();
    String value = "N";
    if(myModel.isShowFinishedVisible()) value = "Y";
    thePreferences.setProperty("tree.showfinished", value);
  }

  private void buildGUI(){
    setLayout(new BorderLayout());
    myTree = new JTree(myModel); 
    myTree.setEditable(false);
    myTree.setDragEnabled(false);
    myTree.setShowsRootHandles(true);
    myTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    ApplicationRefBase.getInstance().put(ApplicationRefBase.TREE, myTree);
    add(myTree, BorderLayout.CENTER);
  }

  public void update(){
    myModel.reload();
  }

  public JTree getTree(){
    return myTree;
  }

  public void eventFired(Event evt) {
    savePreferences();
  }
}
