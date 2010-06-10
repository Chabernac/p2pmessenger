/*
 * Created on 11-mrt-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.test;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;

import chabernac.GUI.WrappingFlowLayout;

public class TestWrappingFlowLayout extends JFrame {
	public TestWrappingFlowLayout(){
		buildGUI();
	}
	
	private void buildGUI(){
		Container theContentPane = getContentPane();
		theContentPane.setLayout(new BorderLayout());
		JPanel theSouthPanel = new JPanel(new WrappingFlowLayout());
		theSouthPanel.setBorder(new EtchedBorder());
		
		for(int i=0;i<10;i++){
			theSouthPanel.add(new JLabel("label: " + i));
		}
		theContentPane.add(theSouthPanel, BorderLayout.SOUTH);
		theContentPane.add(new JTextArea(), BorderLayout.CENTER);
		
	}
	
	public static void main(String args[]){
		TestWrappingFlowLayout theFrame = new TestWrappingFlowLayout();
		theFrame.setSize(300,300);
		theFrame.setVisible(true);
	}

}

