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
   * a File objects must be returned indicating at which place the file must be stored
   * return null when the the file is refused
   * @param aFileName
   * @param aFileId
   * @return
   * @throws FileTransferException
   */
  public File acceptFile(String aFileName, String aFileId);
  public void fileTransfer(String aFileName, String aFileId, Percentage aPercentageComplete);
  public void fileSaved(File aFile) throws FileTransferException;
}
