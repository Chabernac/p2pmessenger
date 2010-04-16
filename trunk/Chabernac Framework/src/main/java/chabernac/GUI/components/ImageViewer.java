package chabernac.GUI.components;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.ImageObserver;

import javax.swing.JFrame;

import chabernac.utils.Debug;


public class ImageViewer extends JFrame
{
	private Image image = null;
	private ImageAdaptor imageAdaptor = null;
	private MouseAdaptor mouseAdaptor = null;
	//private ImageViewer frame = null;
	private Toolkit toolkit = null;
	private boolean locked = false;
	private boolean scaled = false;
	private BorderLayout layout = null;
	private Container container = null;

	public ImageViewer()
	{
		initialize();
		addListeners();
	}

private void initialize()
{
	container = getContentPane();
	layout = new BorderLayout();
	imageAdaptor = new ImageAdaptor();
	mouseAdaptor = new MouseAdaptor();
	toolkit = Toolkit.getDefaultToolkit();
	//frame = this;
	setVisible(true);
}

private void addListeners()
{
		this.addMouseListener(mouseAdaptor);
}




public synchronized void setImage(String imageLoc)
{
	setTitle(imageLoc);
	if(!locked)
	{
		setVisible(true);
		toFront();
		image = null;
		System.gc();
		image = toolkit.getImage(imageLoc);
		if(scaled)
		{
			image = image.getScaledInstance(300,-1,Image.SCALE_FAST);
		}
		repaint();
		/*
		try
		{
		this.wait();
		}catch(Exception e){Debug.log(this,"Could not get Thread in wait state",e);}
		*/
	}
}

private void setLocked(boolean locked){this.locked = locked;}
public boolean isLocked(){return locked;}
public void setScaled(boolean scaled){this.scaled = scaled;}
public boolean isScaled(){return scaled;}


public synchronized void paint(Graphics g)
{
	g.drawImage(image,0,0,imageAdaptor);
}

private synchronized void wakeUp()
{
	try
	{
		this.notify();
	}catch(Exception e){Debug.log(this,"Could not awake thread in wait state",e);}
}





private class ImageAdaptor implements ImageObserver
{
	public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height)
	{
		if(infoflags==ImageObserver.ALLBITS)
		{
			setSize(new Dimension(width,height));
			//wakeUp();
			return false;
		}
		else
		{
			return true;
		}
	}
}


private class MouseAdaptor implements MouseListener
{
	public void mouseClicked(MouseEvent e)
	{
		setLocked(!locked);
	}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mousePressed(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}
}


public void finalize() throws Throwable
{
	image = null;
	imageAdaptor = null;
	mouseAdaptor = null;
	toolkit = null;
	layout = null;
	container = null;
	super.finalize();
}



public static void main(String args[])
{
	ImageViewer viewer = new ImageViewer();
	viewer.setImage(args[0]);
}

}
