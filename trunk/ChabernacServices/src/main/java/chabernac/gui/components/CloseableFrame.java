package chabernac.gui.components;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

public class CloseableFrame extends JFrame implements WindowListener{
  private static final long serialVersionUID = 6615077662449643659L;
  private static final Logger LOGGER = Logger.getLogger(CloseableFrame.class);
  
	public CloseableFrame(){
		super();
		addMyWindowListener();
		LOGGER.error( "Constructor of CloseableFrame called");
	}

	public CloseableFrame(String aTitle){
		super(aTitle);
		addMyWindowListener();
	}

	private void addMyWindowListener(){
		addWindowListener(this);
	}


	/**
		Override to customize
	*/
	public void windowActivated(WindowEvent e){}
	/**
		Override to customize
	*/
	public void windowClosed(WindowEvent e){}
	/**
		Override to customize
	*/
    public void windowClosing(WindowEvent e){}
    /**
		Override to customize
	*/
    public void windowDeactivated(WindowEvent e){}
    /**
		Override to customize
	*/
    public void windowDeiconified(WindowEvent e){}
    /**
		Override to customize
	*/
    public void windowIconified(WindowEvent e){}
    /**
		Override to customize
	*/
	public void windowOpened(WindowEvent e){}

}