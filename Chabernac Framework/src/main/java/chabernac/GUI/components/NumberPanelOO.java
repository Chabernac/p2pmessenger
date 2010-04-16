package chabernac.GUI.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class NumberPanelOO extends JPanel
{

	private JLabel numberlabel= null;
	private JButton plusButton = null;
	private JButton minButton = null;
	private GridBagLayout layout = null;
	private Integer MIN = null;
	private Integer MAX = null;
	private Integer INC = null;
	private Integer CURRENT = null;


	public NumberPanelOO(int min,int max,int inc,Integer current)
	{
		this.MIN = new Integer(min);
		this.MAX = new Integer(max);
		this.INC = new Integer(inc);
		this.CURRENT = current;
		initialize();
		addListeners();
		setupGui();
		numberlabel.setText(CURRENT.toString());
	}

private void initialize()
{
	numberlabel= new JLabel();
	plusButton = new JButton("+");
	minButton = new JButton("-");
	layout = new GridBagLayout();
}



private void setupGui()
{

		setLayout(layout);
		Insets insets = new Insets(2,2,2,2);
		addMyComponent(this,GridBagConstraints.WEST,insets,GridBagConstraints.HORIZONTAL,1,0,1,1,1,1,numberlabel);
		addMyComponent(this,GridBagConstraints.WEST,insets,GridBagConstraints.HORIZONTAL,0,0,2,1,1,1,plusButton);
		addMyComponent(this,GridBagConstraints.WEST,insets,GridBagConstraints.HORIZONTAL,0,0,3,1,1,1,minButton);
}

private void addListeners()
{
	plusButton.addActionListener(new ButtonHandler());
	minButton.addActionListener(new ButtonHandler());
}

private void addMyComponent(Container parent,int anchor, Insets insets, int direction, int weigthx, int weigthy, int gridx, int gridy, int gridwidth, int gridheight, Component component)
{
	GridBagConstraints constraints = new GridBagConstraints();
	constraints.anchor = anchor;
	constraints.insets = insets;
	constraints.fill = direction;
	constraints.weightx = weigthx;
	constraints.weighty = weigthy;
	constraints.gridx = gridx;
	constraints.gridy= gridy;
	constraints.gridwidth = gridwidth;
	constraints.gridheight = gridheight;

	component.setForeground(Color.red);
	//component.setBackground(Color.red);
	layout.setConstraints(component,constraints);
	parent.add(component);
}

/*
public int getNumber()
{
	return CURRENT;
}
*/

public void setNumber(Integer CURRENT)
{
	this.CURRENT = CURRENT;
	numberlabel.setText(CURRENT.toString());
}

private class ButtonHandler implements ActionListener
{
	private int current;
	public void actionPerformed(ActionEvent evt)
	{
		String cmd = evt.getActionCommand();
		current = CURRENT.intValue();
		if(cmd.equals("+"))
		{
//			if(current<MAX.intValue()) CURRENT = CURRENT.intValue() + INC.intValue();
//			numberlabel.setText(Integer.toString(CURRENT));
		}
		else if(cmd.equals("-"))
		{
//			if(CURRENT>MIN.intValue()) CURRENT = CURRENT - INC.intValue();
//			numberlabel.setText(Integer.toString(CURRENT));
		}
	}
}

}




