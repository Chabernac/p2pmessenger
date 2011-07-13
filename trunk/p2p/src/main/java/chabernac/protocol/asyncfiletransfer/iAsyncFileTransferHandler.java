/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.asyncfiletransfer;

import java.io.File;

import chabernac.protocol.filetransfer.FileTransferException;

public interface iAsyncFileTransferHandler {
  public File acceptFile(String aFileName, String aFileId) throws FileTransferException;
  public void fileTransfer(String aFileName, String aFileId, double aPercentageComplete);
  public void fileSaved(File aFile) throws FileTransferException;
}
