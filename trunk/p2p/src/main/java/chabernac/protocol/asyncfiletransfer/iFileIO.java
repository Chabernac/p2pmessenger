/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.asyncfiletransfer;

import java.util.concurrent.ExecutorService;

public interface iFileIO {
  public void start() throws AsyncFileTransferException;
  public void startAsync(ExecutorService aService) throws AsyncFileTransferException;
  public void stop() throws AsyncFileTransferException;
  public void reset() throws AsyncFileTransferException;
  public void waitTillDone() throws AsyncFileTransferException;
  public boolean isComplete();
  public boolean isTransferring();
  public Percentage getPercentageComplete();
}
