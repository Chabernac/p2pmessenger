/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.filetransfer;

import java.io.File;

public interface iFileHandler {
  public File acceptFile(String aFileName);
  public void fileTransfer(File aFile, long aBytesReceived, long aTotalBytes);
  public void fileSaved(File aFile);
  public void fileTransferInterrupted(File aFile);
}
