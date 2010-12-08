/*
 * Created on 12-jan-2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.space;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface Renderable3D {
	public void clip2Frustrum(Frustrum aFrustrum);
	public void world2cam(Camera aCamera);
}
