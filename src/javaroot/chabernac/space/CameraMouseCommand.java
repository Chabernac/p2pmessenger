/*
 * Created on 2-jul-2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.space;

import chabernac.control.MouseCommand;
import chabernac.math.MatrixException;
import chabernac.space.geom.Point3D;
import chabernac.space.geom.Rotation;
import chabernac.utils.Debug;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CameraMouseCommand implements MouseCommand {
	private Camera myTargetCamera = null;
	
	public CameraMouseCommand(Camera aTargetCamera){
		myTargetCamera = aTargetCamera;
	}
	
	/* (non-Javadoc)
	 * @see chabernac.control.MouseCommand#mouseMoved(float, float)
	 */
	public void mouseMoved(float aSpeedX, float aSpeedY) {
		try {
			Camera theRotationCamera = new Camera(new Point3D(0,0,0), new Rotation(0, - aSpeedY / 10000, - aSpeedX / 10000), 1F);
			myTargetCamera.translate(theRotationCamera);
		} catch (MatrixException e) {
			Debug.log(this, "Could not create rotation matrix", e);
		}
	}
	/* (non-Javadoc)
	 * @see chabernac.control.MouseCommand#mouseRightClicked()
	 */
	public void mouseRightClicked() {
		// TODO Auto-generated method stub
	}
	/* (non-Javadoc)
	 * @see chabernac.control.MouseCommand#mouseLeftClicked()
	 */
	public void mouseLeftClicked() {
		// TODO Auto-generated method stub
	}
	/* (non-Javadoc)
	 * @see chabernac.control.MouseCommand#mouseMidClicked()
	 */
	public void mouseMidClicked() {
		// TODO Auto-generated method stub
	}
	/* (non-Javadoc)
	 * @see chabernac.control.MouseCommand#mouseScrollUp()
	 */
	public void mouseScrollUp() {
		// TODO Auto-generated method stub
	}
	/* (non-Javadoc)
	 * @see chabernac.control.MouseCommand#mouseScrollDown()
	 */
	public void mouseScrollDown() {
		// TODO Auto-generated method stub
	}
}
