package chabernac.GUI.components;

import chabernac.command.*;

public class DefaultCommandMenuFrame extends CommandMenuFrame{
	public DefaultCommandMenuFrame(SaveCommand aSaveCommand, OpenCommand aOpenCommand, CloseCommand aCloseCommand){
		super(aSaveCommand, aOpenCommand, aCloseCommand, new DefaultExitCommand());
	}
}
