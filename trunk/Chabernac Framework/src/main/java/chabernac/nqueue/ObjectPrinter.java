/*
 * Created on 9-nov-2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.nqueue;

import chabernac.utils.Debug;

public class ObjectPrinter implements iObjectProcessor {

	public boolean processObject(Object o) {
		Debug.log(this,"Object: " + o.toString());
		return true;
	}

}
