package chabernac.GUI.components;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import chabernac.GUI.utils.GUIUtils;

public class NumberPanel extends JPanel
{

	private JLabel numberlabel= null;
	private JButton plusButton = null;
	private JButton minButton = null;
	private GridBagLayout layout = null;
	private int MIN = 0;
	private int MAX = 0;
	private int INC = 0;
	private int CURRENT =0;
	private NumberPanelListener listener = null;


	public NumberPanel(int min,int max,int inc,int current)
	{
		this.MIN = min;
		this.MAX = max;
		this.INC = inc;
		this.CURRENT = current;
		initialize();
		addListeners();
		setupGui();
		numberlabel.setText(Integer.toString(CURRENT));
	}

private void initialize()
{
	numberlabel= new JLabel();
	Dimension theButtonDimension = new Dimension(10,50);
	plusButton = new JButton("+");
	plusButton.setSize(theButtonDimension);
	minButton = new JButton("-");
	minButton.setSize(theButtonDimension);
	layout = new GridBagLayout();
}



private void setupGui()
{

		setLayout(layout);
		Insets insets = new Insets(2,2,2,2);
		GUIUtils.addMyComponent(this,layout,GridBagConstraints.EAST,insets,GridBagConstraints.NONE,1,0,1,1,1,1,numberlabel,null);
		GUIUtils.addMyComponent(this,layout,GridBagConstraints.CENTER,insets,GridBagConstraints.NONE,0,0,2,1,1,1,minButton,null);
      	GUIUtils.addMyComponent(this,layout,GridBagConstraints.CENTER,insets,GridBagConstraints.NONE,0,0,3,1,1,1,plusButton,null);
}

private void addListeners()
{
	ButtonHandler buttonHandler = new ButtonHandler();
	plusButton.addActionListener(buttonHandler);
	minButton.addActionListener(buttonHandler);
}


public int getNumber()
{
	return CURRENT;
}

public void setNumber(int CURRENT)
{
	this.CURRENT = CURRENT;
	numberlabel.setText(Integer.toString(CURRENT));
}

public void addNumberPanelListener(NumberPanelListener listener)
{
	this.listener = listener;
}

private void numberPanelChanged(){
	if(listener != null){
		listener.numberPanelChanged();
	}
}


private class ButtonHandler implements ActionListener
{
	public void actionPerformed(ActionEvent evt)
	{
		String cmd = evt.getActionCommand();
		if(cmd.equals("+"))
		{
			if(CURRENT + INC <= MAX) CURRENT = CURRENT + INC;
			numberlabel.setText(Integer.toString(CURRENT));
			numberPanelChanged();
		}
		else if(cmd.equals("-"))
		{
			if(CURRENT - INC >= MIN) CURRENT = CURRENT - INC;
			numberlabel.setText(Integer.toString(CURRENT));
			numberPanelChanged();
		}
	}
}

}




