/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.testingutils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import chabernac.protocol.filetransfer.iFileHandler;

public class FileHandler implements iFileHandler {
  private List<File> myReceivedFiles = new ArrayList< File >();
  private List<File> myFailedFiles = new ArrayList< File >();

  @Override
  public File acceptFile( String aFileName ) {
    return new File("received_" + aFileName );
  }

  @Override
  public void fileSaved( File aFile ) {
    myReceivedFiles.add( aFile );
  }

  @Override
  public void fileTransfer( File anAfile, long aBytesReceived, long aTotalBytes ) {
    
  }

  @Override
  public void fileTransferInterrupted( File aFile ) {
    myFailedFiles.add(aFile);
  }

  public List< File > getReceivedFiles() {
    return myReceivedFiles;
  }

  public List< File > getFailedFiles() {
    return myFailedFiles;
  }
}
