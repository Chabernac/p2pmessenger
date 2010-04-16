package chabernac.GUI.components;

import java.awt.event.WindowEvent;
import chabernac.command.*;
import chabernac.utils.*;

public class ExitFrame extends CloseableFrame{

	private Command myExitCommand = null;

	public ExitFrame(Command aCommand){
		super();
		myExitCommand = aCommand;
	}

	//public void windowClosed(WindowEvent e){
	public void windowClosing(WindowEvent e){
		Debug.log(this,"Window close event");
		myExitCommand.execute();
	}
}