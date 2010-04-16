/*
 * Copyright (c) 2003 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */


package chabernac.queue;

/**
 * 
 *
 *
 * @version v1.0.0      Sep 20, 2005
 *<pre><u><i>Version History</u></i>
 *
 * v1.0.0 Sep 20, 2005 - initial release       - Guy Chauliac
 *
 *</pre>
 *
 * @author <a href="mailto:guy.chauliac@axa.be"> Guy Chauliac </a>
 */

public interface iQueueListener {
  public void trigger();
}
