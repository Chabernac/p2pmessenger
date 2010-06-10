/*
 * Created on 24-jan-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.todo.gui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import chabernac.command.AbstractCommand;
import chabernac.event.ApplicationEventDispatcher;
import chabernac.event.Event;
import chabernac.event.iEventListener;
import chabernac.gui.CommandButton;
import chabernac.preference.ApplicationPreferences;
import chabernac.task.Task;
import chabernac.task.TaskTools;
import chabernac.task.event.ApplicationSaveEvent;

public class ToDoPanel extends JPanel implements iEventListener {
	private JTable myTable = null;
	private ArrayList myToDoList = null;
	
	public ToDoPanel(){
		init();
		buildGUI();
    loadPreferences();
	}
	
	private void init(){
		myTable = new JTable(new ToDoModel());
    myTable.setCellSelectionEnabled(false);
    myTable.setRowSelectionAllowed(true);
		myToDoList = TaskTools.getToDoList();
    ApplicationEventDispatcher.addListener(this, ApplicationSaveEvent.class);
	}
	
	private void buildGUI(){
		setLayout(new BorderLayout());
		add(new JScrollPane(myTable), BorderLayout.CENTER);
		JPanel theSouthPanel = new JPanel(new GridLayout(1,-1));
		theSouthPanel.add(new CommandButton(new UpCommand()));
		theSouthPanel.add(new CommandButton(new DownCommand()));
		theSouthPanel.add(new CommandButton(new StartCommand()));
		theSouthPanel.add(new CommandButton(new RemoveCommand()));
		add(theSouthPanel, BorderLayout.SOUTH);
	}
	
	public void paint(Graphics g){
	    myTable.tableChanged(new TableModelEvent(myTable.getModel()));
	    super.paint(g);
	  }
  
   public void eventFired(Event evt) {
     savePreferences();
   }
   
   private void savePreferences(){
     ApplicationPreferences thePreferences = ApplicationPreferences.getInstance();
     TableColumnModel theColumnModel = myTable.getColumnModel();
     for(int i=0;i<theColumnModel.getColumnCount();i++){
       TableColumn theColumn = theColumnModel.getColumn(i);
       thePreferences.setProperty("todo." +  theColumn.getHeaderValue() + ".width", Integer.toString(theColumn.getWidth()));
     }
   }
   
   private void loadPreferences(){
     ApplicationPreferences thePreferences = ApplicationPreferences.getInstance();
     TableColumnModel theColumnModel = myTable.getColumnModel();
     for(int i=0;i<myTable.getModel().getColumnCount();i++){
       String theColumnName = myTable.getModel().getColumnName(i);
       int theWidth = Integer.parseInt(thePreferences.getProperty("todo." + theColumnName + ".width", "-1"));
       TableColumn theColumn = theColumnModel.getColumn(i);
       if(theWidth != -1) theColumn.setPreferredWidth(theWidth);
       theColumn.setHeaderValue(myTable.getModel().getColumnName(i));
     }
   }
	
	private abstract class AbstractToDoCommand extends AbstractCommand{
		
		public AbstractToDoCommand(){
			myTable.addMouseListener(new MouseAdapter(){
				public void mousePressed(MouseEvent evt){
					notifyObs();
				}
			});
		}
		
		public boolean isEnabled() {
			if(myTable.getSelectedRow() == -1) return false;
			return true;
		}
	}
	
	private class UpCommand extends AbstractToDoCommand{
		public void execute() {
			int theRow = myTable.getSelectedRow();
			int theDestination = theRow - 1;
			if(theDestination < 0) return;
			Task theTask = (Task)myToDoList.get(theRow);
			myToDoList.remove(theRow);
			myToDoList.add(theDestination, theTask);
			myTable.tableChanged(new TableModelEvent(myTable.getModel()));
			myTable.setRowSelectionInterval(theDestination, theDestination);
		}

		public String getName() {
			return "Up";
		}
	}

	private class DownCommand extends AbstractToDoCommand{
		public void execute() {
			int theRow = myTable.getSelectedRow();
			int theDestination = theRow + 1;
			if(theDestination >= myToDoList.size()) return;
			
			Task theTask = (Task)myToDoList.get(theRow);
			myToDoList.remove(theRow);
			myToDoList.add(theDestination, theTask);
			myTable.tableChanged(new TableModelEvent(myTable.getModel()));
			myTable.setRowSelectionInterval(theDestination, theDestination);
		}

		public String getName() {
			return "Down";
		}
	}
	
	private class StartCommand extends AbstractToDoCommand{
		public void execute() {
			int theRow = myTable.getSelectedRow();
      if(theRow != -1){
  			Task theTask = (Task)myToDoList.get(theRow);
  			TaskTools.startTask(theTask);
  			notifyObs();
      }
		}

		public String getName() {
			return "Start";
		}
		
		public boolean isEnabled(){
			if(!super.isEnabled()) return false;
			int theRow = myTable.getSelectedRow();
			Task theTask = (Task)myToDoList.get(theRow);
			if(!theTask.isRunning()) return true;
			return false;
		}
	}

	private class RemoveCommand extends AbstractToDoCommand{
		public void execute() {
			int theRow = myTable.getSelectedRow();
			Task theTask = (Task)myToDoList.get(theRow);
			myToDoList.remove(theTask);
			myTable.tableChanged(new TableModelEvent(myTable.getModel()));
		}

		public String getName() {
			return "Remove";
		}
    
	}

}
