/*
 * Created on 13-jul-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package chabernac.control;

import java.io.Serializable;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface iSynchronizedEvent extends Serializable{
  /**
   * return true when this event has effectively run
   */
	public boolean executeEvent(long aCounter);
	
	/**
	 * return true when this event can be recorded 
	 */
	public boolean isRecordable();
}
