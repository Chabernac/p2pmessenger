package chabernac.gui.components;

import java.awt.event.WindowEvent;

import org.apache.log4j.Logger;

import chabernac.command.Command;

public class ExitFrame extends CloseableFrame{
  private static final Logger LOGGER = Logger.getLogger(ExitFrame.class);

	private Command myExitCommand = null;

	public ExitFrame(Command aCommand){
		super();
		myExitCommand = aCommand;
	}

	//public void windowClosed(WindowEvent e){
	public void windowClosing(WindowEvent e){
		LOGGER.error( "Window close event");
		myExitCommand.execute();
	}
}