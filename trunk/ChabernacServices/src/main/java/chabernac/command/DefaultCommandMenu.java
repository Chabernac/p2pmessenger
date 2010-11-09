package chabernac.command;

import javax.swing.JMenu;

public class DefaultCommandMenu extends JMenu{

  private static final long serialVersionUID = -7341447978498583916L;

  public DefaultCommandMenu(SaveCommand aSaveCommand, OpenCommand aOpenCommand, CloseCommand aCloseCommand, ExitCommand aExitCommand){
		super("File");
		if (aSaveCommand != null) { add(new CommandMenuItem(new ButtonCommandDecorator( aSaveCommand, "Save", 's'))); }
		if (aOpenCommand != null) { add(new CommandMenuItem(new ButtonCommandDecorator( aOpenCommand, "Open", 'o'))); }
		if (aCloseCommand != null){ add(new CommandMenuItem(new ButtonCommandDecorator( aCloseCommand, "Close", 'c'))); }
		if (aExitCommand != null) { add(new CommandMenuItem(new ButtonCommandDecorator( aExitCommand, "Exit", 'e'))); }
	}

}