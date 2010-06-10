/*
 * Created on 9-feb-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.object;

public class DefaultObjectIdConvertor implements iObjectIdConvertor {

	public int getObjectId(Object anObject) {
		return anObject.toString().intern().hashCode();
	}

}
