/*
 * Created on 24-dec-2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.space;

import org.apache.log4j.Logger;

import chabernac.control.SynchronizedEventManager;
import chabernac.control.SynchronizedKeyCommand;
import chabernac.math.MatrixException;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CameraMoveCommand extends SynchronizedKeyCommand {
  private static Logger LOGGER = Logger.getLogger(CameraMoveCommand.class);
  
	private Camera myTargetCamera = null;
	private Camera myTranslationCamera = null;

	/**
	 * @param aDescription
	 * @param aTimer
	 */
	public CameraMoveCommand(String aDescription, SynchronizedEventManager aManager, Camera aTargetCamera, Camera aTranslationCamera) {
		this(aDescription, aManager, 1, aTargetCamera, aTranslationCamera);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param aDescription
	 * @param aTimer
	 * @param aTimerInterval
	 */
	public CameraMoveCommand( String aDescription, SynchronizedEventManager aManager, int aTimerInterval, Camera aTargetCamera, Camera aTranslationCamera){
		super(aDescription, aManager, aTimerInterval);
		myTargetCamera = aTargetCamera;
		myTranslationCamera = aTranslationCamera;		
	}

	/* (non-Javadoc)
	 * @see chabernac.control.KeyCommand#keyDown()
	 */
	public void keyDown() {
		try {
			myTargetCamera.translate(myTranslationCamera);
		}catch(MatrixException e){ 
			LOGGER.error("Error in keyDown", e);
		}
	}

}
