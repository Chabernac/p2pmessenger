/*
 * Created on 13-feb-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JMenuItem;

import chabernac.command.AbstractCommand;

public class CommandMenuItem extends JMenuItem implements Observer, ActionListener{
  private static final long serialVersionUID = 2145957926171156640L;
  private AbstractCommand myCommand = null;
	
	public CommandMenuItem(AbstractCommand aCommand){
		myCommand = aCommand;
		layoutMenu();
		myCommand.addObserver(this);
		addActionListener(this);
	}
		
	
	private void layoutMenu(){
		setText(myCommand.getName());
		setMnemonic(myCommand.getMnemonic());
		setEnabled(myCommand.isEnabled());
	}


	public void update(Observable o, Object arg) {
		layoutMenu();
	}


	public void actionPerformed(ActionEvent e) {
		myCommand.execute();
	}
}
