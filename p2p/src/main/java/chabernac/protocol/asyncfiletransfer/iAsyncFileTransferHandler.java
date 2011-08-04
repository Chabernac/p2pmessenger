/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.asyncfiletransfer;

import java.io.File;

import chabernac.protocol.filetransfer.FileTransferException;

public interface iAsyncFileTransferHandler {
  /**
   * this method will be executed when an incoming file is detected
   * you must call iTransferController.acceptFileTransfer to start the file transfer
   * or return inmediately the accepted File
   */
  public File acceptFile(String aFileName, String aFileId, iTransferController aController);
  public void fileTransfer(String aFileName, String aFileId, Percentage aPercentageComplete);
  public void fileSaved(File aFile) throws FileTransferException;
}
