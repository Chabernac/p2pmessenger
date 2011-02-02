package chabernac.gui;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class ShutDownDialog extends JDialog {
	private JLabel myLabel = null;

	public ShutDownDialog(JFrame aFrame) {
		super(aFrame, "Shutting down", false );
		setLocation(aFrame.getX() + aFrame.getWidth() / 2, aFrame.getY() + aFrame.getHeight() / 2);
		init();
		buildGUI();
	}
	
	private void init(){
		myLabel = new JLabel();
	}
	
	private void buildGUI(){
		Container theContentPane = getContentPane();
    theContentPane.setLayout(new BorderLayout());
    theContentPane.add(myLabel, BorderLayout.CENTER);
    setSize(200, 60);
    //pack();
	}
	
	public void setMessage(final String aMessage){
	  SwingUtilities.invokeLater( new Runnable(){
	    public void run(){
	      myLabel.setText(aMessage);
	    }
	  });
	}

}
