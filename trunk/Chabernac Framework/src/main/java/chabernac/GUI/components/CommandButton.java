/*
 * Created on 13-dec-07
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.GUI.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import chabernac.command.Command;

public class CommandButton extends JButton implements ActionListener{
	private Command myCommand = null;
	
	public CommandButton(String aText, Command aCommand){
		super(aText);
		myCommand = aCommand;
		addActionListener(this);
	}

	public void actionPerformed(ActionEvent arg0) {
		myCommand.execute();
	}

}
