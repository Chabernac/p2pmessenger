/*
 * Created on 27-dec-2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.control;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class KeyConfigurationDialog extends JDialog {
	private KeyMapContainer myContainer = null;

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @throws java.awt.HeadlessException
	 */
	public KeyConfigurationDialog(Frame owner, String title, boolean modal, KeyMapContainer aContainer)
		throws HeadlessException {
		super(owner, title, modal);
		myContainer = aContainer;
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(new KeyConfigurationPanel(myContainer), BorderLayout.CENTER);
		buildOKButton();
		addWindowListener(new MyWindowAdaptor());
		pack();
		setResizable(false);
	}
	
	private void buildOKButton(){
		JButton theButton = new JButton("OK");
		theButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				myContainer.notifyAllObs();
				dispose(); 
			}
		});
		theButton.setMnemonic(KeyEvent.VK_O);
		getContentPane().add(theButton, BorderLayout.SOUTH);
	}
	
	private class MyWindowAdaptor extends WindowAdapter{
		public void windowClosing(WindowEvent evt){
			myContainer.notifyAllObs();
		}
	}
	
}