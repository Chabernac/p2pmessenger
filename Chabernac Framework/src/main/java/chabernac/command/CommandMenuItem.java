package chabernac.command;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JMenuItem;

public class CommandMenuItem extends JMenuItem{
	public CommandMenuItem(String aText, Command aCommand){
		super(aText);
		addMenuItemListener(aCommand);
	}

	private void addMenuItemListener(final Command aCommand){
		addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				aCommand.execute();
			}
		});
	}

}