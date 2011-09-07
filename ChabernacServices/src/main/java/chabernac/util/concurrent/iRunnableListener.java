/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.util.concurrent;

public interface iRunnableListener {
  public void statusChanged(MonitorrableRunnable.Status aStatus, String anExtraInfo);
}
