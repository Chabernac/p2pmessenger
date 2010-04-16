/*
 * Created on 16-jul-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package chabernac.space;

import chabernac.space.geom.Point3D;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface iTranslatable {
	public void translate(Camera aCamera) throws TranslateException;
	public Point3D getCenterPoint();
}
