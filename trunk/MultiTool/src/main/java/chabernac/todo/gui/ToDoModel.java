/*
 * Created on 24-jan-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.todo.gui;

import java.util.ArrayList;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import chabernac.task.Task;
import chabernac.task.TaskTools;

public class ToDoModel implements TableModel {
	private ArrayList myToDoList = null;
	
	public ToDoModel(){
		myToDoList = TaskTools.getToDoList();;
	}

	public int getRowCount() {
		return myToDoList.size();
	}

	public int getColumnCount() {
		return 3;
	}

	public String getColumnName(int columnIndex) {
		switch(columnIndex){
			case 0: return "Taak";
      case 1: return "Behoort tot";
			case 2: return "Gewerkt";
		}
		return "";
	}

	public Class getColumnClass(int columnIndex) {
		return String.class;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		Task theTask = (Task)myToDoList.get(rowIndex);

		switch(columnIndex){
			case 0: return theTask.getName();
      case 1: {
        if(theTask.getParentTask() != null){
          return theTask.getParentTask().getFullName();
        }
        return "";
      }
			case 2: return TaskTools.formatTimeInHours(theTask.getTimeWorked()); 
		}
		return null;
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	
	}

	public void addTableModelListener(TableModelListener l) {
		
	}

	public void removeTableModelListener(TableModelListener l) {
		
	}

}
