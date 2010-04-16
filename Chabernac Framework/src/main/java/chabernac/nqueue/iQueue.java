/*
 * Created on 9-nov-2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.nqueue;

public interface iQueue {
	public Object get();
	public void put(Object o);
	public void setPutEnabled(boolean putEnabled);
	public boolean isPutEnabled();
	public void setGetEnabled(boolean getEnabled);
	public boolean isGetEnabled();
	public int size();
	public boolean isEmpty();
	public void clear();
}
