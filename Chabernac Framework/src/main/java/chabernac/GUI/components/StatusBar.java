package chabernac.GUI.components;

import javax.swing.JTextField;
import java.awt.Color;
import chabernac.utils.Debug;

public class StatusBar extends JTextField implements Runnable
{
	private boolean scrolling = false;
	//private String spaces = "                                                                                                                                                                                                       ";
	private String spaces = "    ";
	private String originalText;
	private int length = 0;
	private int nrSpaces = 0;
	private int interval = 300;
	private int timeout = 2000;

	public StatusBar()
	{
		super();
		setupGui();
	}

private void setupGui()
{
	setBackground(Color.lightGray);
	setForeground(Color.red);
	setEditable(false);
}

//public synchronized void run()
public void run()
{
	try
	{
		length = spaces.length();
		while(scrolling)
		{
			if(getWidth() > 0 && nrSpaces != (getWidth() / 4)){makeSpaces();}
			setText(spaces.substring(0,length) + originalText);
			length--;
			if(length<=0)
			{
				length = spaces.length();
				Thread.sleep(timeout);
				Thread.yield();
			}
			else
			{
				Thread.sleep(interval);
				Thread.yield();
			}
		}
		wakeUp();

	}catch(Exception e)
		{
			Debug.log(this,"Exception occured while scrolling",e);
			stopScrolling();
		}
}

private synchronized void wakeUp()
{
	notifyAll();
}

//public synchronized void startScrolling()
public synchronized void startScrolling()
{
	makeSpaces();
	originalText = getText();
	if(scrolling)
		{
			/*
			scrolling = false;
			try
			{
			wait();
			}catch(Exception e){Debug.log(this,"Could not get object in wait state",e);}
			*/
		}
	else
		{
			scrolling = true;
			new Thread(this).start();
		}
}


private void makeSpaces()
{
	nrSpaces = getWidth() / 4;
	//length = nrSpaces - 1;
	length = 0;
	StringBuffer buffer = new StringBuffer();
	for(int i=0;i<nrSpaces;i++)
	{
		buffer.append(" ");
	}
	spaces = buffer.toString();
}

public void setInterval(int interval){this.interval = interval;}
public int getInterval(){return interval;}
public void setTimeout(int timeout){this.timeout = timeout;}
public int getTimeout(){return timeout;}

public synchronized void stopScrolling()
{
	if(scrolling)
	{
		scrolling = false;
		try
		{
				wait();
		}catch(Exception e){Debug.log(this,"Could not get object in wait state",e);}
		setText(originalText);
	}
}

public synchronized boolean isScrolling(){return scrolling;}

}
