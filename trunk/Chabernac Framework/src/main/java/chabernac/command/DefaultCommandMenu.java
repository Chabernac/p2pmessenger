package chabernac.command;

import javax.swing.*;

public class DefaultCommandMenu extends JMenu{

	public DefaultCommandMenu(SaveCommand aSaveCommand, OpenCommand aOpenCommand, CloseCommand aCloseCommand, ExitCommand aExitCommand){
		super("File");
		if (aSaveCommand != null) { add(new CommandMenuItem("Save", aSaveCommand)); }
		if (aOpenCommand != null) { add(new CommandMenuItem("Open", aOpenCommand)); }
		if (aCloseCommand != null){ add(new CommandMenuItem("Close", aCloseCommand)); }
		if (aExitCommand != null) { add(new CommandMenuItem("Exit", aExitCommand)); }
	}

}