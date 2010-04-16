package chabernac.GUI.components;

import javax.swing.JMenuBar;

import chabernac.command.CloseCommand;
import chabernac.command.DefaultCommandMenu;
import chabernac.command.ExitCommand;
import chabernac.command.OpenCommand;
import chabernac.command.SaveCommand;

public class CommandMenuFrame extends ExitFrame{
	public CommandMenuFrame(SaveCommand aSaveCommand, OpenCommand aOpenCommand, CloseCommand aCloseCommand, ExitCommand aExitCommand){
		super(aExitCommand);
		setupMenuBar(aSaveCommand, aOpenCommand, aCloseCommand, aExitCommand);
	}

	private void setupMenuBar(SaveCommand aSaveCommand, OpenCommand aOpenCommand, CloseCommand aCloseCommand, ExitCommand aExitCommand){
		JMenuBar theMenuBar = new JMenuBar();
		theMenuBar.add(new DefaultCommandMenu(aSaveCommand, aOpenCommand, aCloseCommand, aExitCommand));
		setJMenuBar(theMenuBar);
	}
}
