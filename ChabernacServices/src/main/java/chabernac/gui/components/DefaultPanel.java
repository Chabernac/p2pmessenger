/*
 * Created on 13-dec-07
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.gui.components;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JPanel;

import chabernac.command.Command;


public abstract class DefaultPanel extends JPanel {
	private boolean isOKVisible = true;
	private boolean isCancelVisible = true;
	
	public DefaultPanel(boolean isOKVisible, boolean isCancelVisible){
		this.isOKVisible = isOKVisible;
		this.isCancelVisible = isCancelVisible;
	}
	
	protected void buildGUI(){
		setLayout(new BorderLayout());
		
		JPanel theButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		
		if(isOKVisible){
			theButtonPanel.add(new CommandButton("Ok", new OKCommand()));
		}
		
		if(isCancelVisible){
			theButtonPanel.add(new CommandButton("Cancel", new CancelCommand()));
		}
		
		add(theButtonPanel, BorderLayout.SOUTH);
		
		JPanel theCenterPanel = new JPanel();
		buildPanel(theCenterPanel);
		add(theCenterPanel, BorderLayout.CENTER);
	}
	
	protected abstract void buildPanel(JPanel aPanel);
	protected abstract void okPressed();
	protected abstract void cancelPressed();
	
	private class OKCommand implements Command{
		public void execute() {
			okPressed();
		}
	}
	
	private class CancelCommand implements Command{
		public void execute() {
			cancelPressed();
		}
	}

	
	

}
