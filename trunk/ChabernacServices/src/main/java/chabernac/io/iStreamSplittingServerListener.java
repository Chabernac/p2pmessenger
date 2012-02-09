/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

public interface iStreamSplittingServerListener {
  public void streamSplittingServerStarted(int aPort, iSocketSender aSocketSender);
  public void streamSplittingServerStopped();
}
