/*
 * Created on 24-jan-08
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.easteregg;

public interface iEasterEgg {
	public void setParameter(Object aParameter);
	public void start();
	public boolean isRunning();
	public void stop();
	public void setEasterEggListener(iEasterEggListener aListener);
}
