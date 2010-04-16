package chabernac.GUI.components;

import java.awt.*;
import javax.swing.*;
import chabernac.GUI.utils.*;

public class ErrorWindow extends JFrame
{
	private JScrollPane myScrollPane = null;
	private JTextField myTextField = null;
	private GridBagLayout myLayout = null;

	public ErrorWindow(Throwable e)
	{
		initialize();
		setupGui();
		myTextField.setText(e.toString());
	}

	private void initialize()
	{
		myTextField = new JTextField();
		myScrollPane = new JScrollPane(myTextField);
		myLayout = new GridBagLayout();
	}

	private void setupGui()
	{
		getContentPane().setLayout(myLayout);
		Insets insets = new Insets(1,1,1,1);
		GUIUtils.addMyComponent(getContentPane(),myLayout,GridBagConstraints.WEST,insets,GridBagConstraints.BOTH,1,1,1,1,1,1,myTextField,Color.black);
		setTitle("An error occured");
		myTextField.setForeground(Color.red);
		pack();
		//setSize(200,200);
	}
}
