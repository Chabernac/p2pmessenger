package chabernac.queue;

import java.util.Vector;
import java.io.File;
import chabernac.GUI.components.*;
import chabernac.utils.*;

public class QueueImageViewer extends QueueObserver
{
	private int simultanousViews = 1;
	private Vector views = null;
	private int current = 0;
	private int tries = 0;
	private boolean scaled = false;

	public QueueImageViewer(Queue imageQueue)
	{
		super(imageQueue);
		setPriority(Thread.MAX_PRIORITY);
		intialize();
	}

	private void intialize()
	{
		views = new Vector(3,2);
	}


	public synchronized void setSimultanousViews(int simultanousViews)
	{
		this.simultanousViews = simultanousViews;
		//setCorrectNumberOfViews();
	}
	public int getSimultanousViews(){return simultanousViews;}

	public synchronized void processObject(Object o)
	{
		if(o instanceof File)
		{
			o = ((File)o).toString();
		}
		if(o instanceof String)
		{
			Debug.log(this,"Displaying: " + (String)o);
			setCorrectNumberOfViews();
			tries = 0;
			while(tries < views.size())
			{
				tries++;
				ImageViewer viewer = (ImageViewer)views.elementAt(current);
				viewer.setScaled(scaled);
				current++;
				if(current >= views.size())
				{
					current = 0;
				}
				if(!viewer.isLocked())
				{
					viewer.setImage((String)o);
					break;
				}
			}
		}
		else
		{
			Debug.log(this,"Can not view this object, not a file");
		}

	}

	private void setCorrectNumberOfViews()
	{
		while(views.size() < simultanousViews)
		{
			views.addElement(new ImageViewer());
		}
		while(views.size() > simultanousViews )
		{
			ImageViewer viewer = (ImageViewer)views.elementAt(0);
			viewer.dispose();
			views.removeElementAt(0);
			current = 0;
		}
	}

	public void setScaled(boolean scaled){this.scaled = scaled;}
	public boolean isScaled(){return scaled;}
	public void setPaused(boolean paused)
	{
		super.setPaused(paused);
		ImageViewer viewer = null;
		for(int i=0;i<views.size();i++)
		{
			viewer = (ImageViewer)views.elementAt(i);
			//viewer.setVisible(!paused);
			if(paused){viewer.dispose();}
			else{viewer.setVisible(true);}
		}
		if(!paused){trigger();}
		else
		{
			//Thread.yield();
			System.gc();
		}

	}

}




