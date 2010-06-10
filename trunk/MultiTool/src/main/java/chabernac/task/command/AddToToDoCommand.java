/*
 * Created on 24-jan-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.task.command;

import java.util.ArrayList;

import chabernac.task.Task;
import chabernac.task.TaskTools;

public class AddToToDoCommand extends ActivityCommand {

	protected void executeCommand() {
		ArrayList theToDoList = TaskTools.getToDoList();
		Task theSelectedTask = getSelectedTask();
		if(theToDoList.contains(theSelectedTask)){
			theToDoList.remove(theSelectedTask);
		} else {
			theToDoList.add(theSelectedTask);
		}
	}

	public String getName() {
		Task theSelectedTask = getSelectedTask();
		ArrayList theToDoList = TaskTools.getToDoList();
		if(theSelectedTask != null && theToDoList.contains(theSelectedTask)){
			return "Remove todo";
		} else {
			return "Add todo";
		}
		
	}

	public boolean isEnabled() {
		Task theSelectedTask = getSelectedTask();
	    if(theSelectedTask == null) return false;
	    if(theSelectedTask.getChildCount() > 0) return false;
	    return true;
	}

}
