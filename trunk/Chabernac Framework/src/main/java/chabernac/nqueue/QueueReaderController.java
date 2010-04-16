package chabernac.nqueue;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import chabernac.GUI.components.NumberPanel;
import chabernac.GUI.components.NumberPanelListener;
import chabernac.GUI.utils.GUIUtils;



public class QueueReaderController extends JPanel
{
	QueueReader queueReader = null;

	private JLabel intervalL = null;
	private JLabel threadsL = null;
	private JCheckBox pause = null;
	private NumberPanel interval = null;
	private NumberPanel threads = null;
	private JButton clear = null;
	private GridBagLayout layout = null;
//	private int x = 1;
//	private int y = 1;
	private boolean pauseEnabled = true;
	private boolean intervalEnabled = true;
	private boolean threadsEnabled = true;
	private boolean clearEnabled = true;
	private ButtonListener buttonListener = null;
	private NumberPanelAdaptor numberPanelAdaptor = null;

	public QueueReaderController(QueueReader queueReader, boolean pauseEnabled, boolean intervalEnabled, boolean threadsEnabled, boolean clearEnabled)
	{
		this.queueReader = queueReader;
		this.pauseEnabled = pauseEnabled;
		this.intervalEnabled = intervalEnabled;
		this.threadsEnabled = threadsEnabled;
		this.clearEnabled = clearEnabled;
		initialize();
		setupGui();
		setSettings();
		checkState();
	}

	public QueueReaderController(QueueReader queueReader)
	{
		this(queueReader, true, true, true, true);
	}


	private void initialize()
	{
		layout = new GridBagLayout();
		buttonListener = new ButtonListener();
		numberPanelAdaptor = new NumberPanelAdaptor();
	}




	private void setupGui()
	{
		//ButtonListener buttonListener = new ButtonListener();
		//NumberPanelAdaptor numberPanelAdaptor = new NumberPanelAdaptor();

		setLayout(layout);

		int x=1, y=1;

		if(pauseEnabled)
		{
			GUIUtils.addMyComponent(this,layout,GridBagConstraints.WEST,new Insets(2,2,2,2),GridBagConstraints.HORIZONTAL,1,0,x,y++,1,1,buildOptionPanel(),Color.black);
		}
		if(intervalEnabled || threadsEnabled)
		{
			GUIUtils.addMyComponent(this,layout,GridBagConstraints.WEST,new Insets(2,2,2,2),GridBagConstraints.HORIZONTAL,1,0,x,y++,1,1,buildNumberPanel(),Color.black);
		}
		if(clearEnabled)
		{
			GUIUtils.addMyComponent(this,layout,GridBagConstraints.WEST,new Insets(2,2,2,2),GridBagConstraints.HORIZONTAL,1,0,x,y++,1,1,buildButtonPanel(),Color.black);
		}
	}

	private JPanel buildOptionPanel()
	{
		JPanel optionPanel = new JPanel();
		//optionPanel.setBorder(new TitledBorder("Options"));
		Insets insets = new Insets(1,1,1,1);
		GridBagLayout optionPanelLayout = new GridBagLayout();
		optionPanel.setLayout(optionPanelLayout);

		int x=1, y=1;

		if(pauseEnabled)
		{
			pause = new JCheckBox("Pause");
			pause.addActionListener(buttonListener);
			GUIUtils.addMyComponent(optionPanel,optionPanelLayout,GridBagConstraints.WEST,insets,GridBagConstraints.NONE,1,0,x++,y,1,1,pause,Color.black);
		}
		return optionPanel;
	}

	private JPanel buildNumberPanel()
	{
		JPanel numberPanel = new JPanel();
		//optionPanel.setBorder(new TitledBorder("Options"));
		Insets insets = new Insets(1,1,1,1);
		GridBagLayout numberPanelLayout = new GridBagLayout();
		numberPanel.setLayout(numberPanelLayout);

		int x=1, y=1;

		if(intervalEnabled)
		{
			intervalL = new JLabel("Interval");
			interval = new NumberPanel(100,1000000,100,1000);
			interval.addNumberPanelListener(numberPanelAdaptor);
			GUIUtils.addMyComponent(numberPanel,numberPanelLayout,GridBagConstraints.WEST,insets,GridBagConstraints.NONE,0,0,x++,y,1,1,intervalL,Color.black);
			GUIUtils.addMyComponent(numberPanel,numberPanelLayout,GridBagConstraints.WEST,insets,GridBagConstraints.NONE,1,0,x,y++,1,1,interval,Color.black);
		}
		if(threadsEnabled)
		{
			threadsL = new JLabel("Threads");
			threads = new NumberPanel(1,20,1,1);
			threads.addNumberPanelListener(numberPanelAdaptor);
			x = 1;
			GUIUtils.addMyComponent(numberPanel,numberPanelLayout,GridBagConstraints.WEST,insets,GridBagConstraints.NONE,0,0,x++,y,1,1,threadsL,Color.black);
			GUIUtils.addMyComponent(numberPanel,numberPanelLayout,GridBagConstraints.WEST,insets,GridBagConstraints.NONE,1,0,x,y++,1,1,threads,Color.black);
		}
		return numberPanel;

	}

	private JPanel buildButtonPanel()
	{
		JPanel buttonPanel = new JPanel();
		//optionPanel.setBorder(new TitledBorder("Options"));
		Insets insets = new Insets(1,1,1,1);
		GridBagLayout buttonPanelLayout = new GridBagLayout();
		buttonPanel.setLayout(buttonPanelLayout);

		int x=1, y=1;

		if(clearEnabled)
		{
			clear = new JButton("Clear");
			clear.addActionListener(buttonListener);
			GUIUtils.addMyComponent(buttonPanel,buttonPanelLayout,GridBagConstraints.WEST,insets,GridBagConstraints.HORIZONTAL,1,0,x++,y,1,1,clear,Color.black);
		}
		return buttonPanel;
	}

	/*
	private void addComponents(Component label, Component field)
	{
		GUIUtils.addMyComponent(this,layout,GridBagConstraints.WEST,new Insets(2,2,2,2),GridBagConstraints.NONE,1,0,x,y,1,1,field,Color.black);
		GUIUtils.addMyComponent(this,layout,GridBagConstraints.WEST,new Insets(2,2,2,2),GridBagConstraints.NONE,1,0,++x,y,1,1,label,Color.black);
		x = 1;
		y++;
	}
	*/

	private class ButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			if(evt.getActionCommand().equals(clear.getActionCommand()))
			{
				queueReader.getQueue().clear();
			}
			else
			{
			//checkState();
			applyNewSettings();
			}
		}
	}

	private class NumberPanelAdaptor implements NumberPanelListener
	{
		public void numberPanelChanged()
		{
			//Debug.log(this,"applying new settings");
			applyNewSettings();
		}
	}

	private void checkState()
	{
	}

	//Read settings from the model
	private void setSettings()
	{
		if(intervalEnabled){interval.setNumber(queueReader.getTimeout());}
		if(threadsEnabled){threads.setNumber(queueReader.getThreads());}
		if(pauseEnabled){pause.setSelected(queueReader.isPaused());}
	}

	//Write new settings to model
	private void applyNewSettings()
	{
		if(intervalEnabled){queueReader.setTimeout(interval.getNumber());}
		if(threadsEnabled){queueReader.setThreads(threads.getNumber());}
		if(pauseEnabled)
		{
			queueReader.setPaused(pause.isSelected());
			if(!pause.isSelected()){queueReader.trigger();}
		}
	}

}