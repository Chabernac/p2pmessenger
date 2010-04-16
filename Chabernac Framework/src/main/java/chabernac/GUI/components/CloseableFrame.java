package chabernac.GUI.components;

import javax.swing.JFrame;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import chabernac.utils.*;

public class CloseableFrame extends JFrame implements WindowListener{
	public CloseableFrame(){
		super();
		addMyWindowListener();
		Debug.log(this,"Constructor of CloseableFrame called");
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