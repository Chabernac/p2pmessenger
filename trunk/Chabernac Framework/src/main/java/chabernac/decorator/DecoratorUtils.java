/*
 * Created on 11-nov-2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.decorator;

public class DecoratorUtils {
	public static Object findDecoratedObject(iDecorator aDecorator, Class aClass){
		iDecorator theDecorator = aDecorator;
		while(!theDecorator.getClass().equals(aClass) && theDecorator != null){
			Object theDecoratedObject = aDecorator.getDecoratedObject();
			if(theDecoratedObject instanceof iDecorator){
				theDecorator = (iDecorator)theDecorator;
			} else {
				theDecorator = null;
			}
		}
		return theDecorator;
	}

}
