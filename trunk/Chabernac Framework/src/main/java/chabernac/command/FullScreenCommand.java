/*
 * Created on 13-jun-2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.command;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;

import chabernac.control.KeyCommand;
import chabernac.utils.Debug;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class FullScreenCommand extends KeyCommand {
	private Component myComponent = null;
	private boolean inFullScreen = false;
	
	public FullScreenCommand(Component aComponent){
		super("Full screen");
		myComponent = aComponent;
	}
	
	
	public void keyPressed() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gs = ge.getDefaultScreenDevice();
		if(!inFullScreen){
			Debug.log(this,"Going to full screen...");
	        Frame frame = new Frame(gs.getDefaultConfiguration());
	        Window aWindow = new Window(frame);
	        aWindow.add(myComponent, BorderLayout.CENTER);
	        gs.setFullScreenWindow(aWindow);
	        aWindow.validate();
	        inFullScreen = true;
		} else {
			gs.setFullScreenWindow(null);
			inFullScreen = false;
		}
	}
	
	public void keyDown() {}
	public void keyReleased() {}
}
