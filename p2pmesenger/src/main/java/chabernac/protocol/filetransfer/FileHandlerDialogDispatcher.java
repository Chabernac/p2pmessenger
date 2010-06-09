/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.filetransfer;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FileHandlerDialogDispatcher implements iFileHandler {
  private Map<File, FileHandlerDialog> myDialogs = Collections.synchronizedMap( new HashMap< File, FileHandlerDialog >());
  private int myMinDimension;
  
  public FileHandlerDialogDispatcher(){
    Dimension theScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
    myMinDimension = theScreenSize.width;
    if(theScreenSize.height < myMinDimension){
      myMinDimension = theScreenSize.height;
    }
  }

  @Override
  public File acceptFile( String aFileName ) {
    FileHandlerDialog theDialog = new FileHandlerDialog();
    
    int theLocation = (myDialogs.size() * 20 + 20) % myMinDimension;
    theDialog.setLocation( theLocation, theLocation );
    File theFile = theDialog.acceptFile( aFileName );
    myDialogs.put(theFile, theDialog);
    addCloseListener(theFile, theDialog);
    return theFile;
  }

  private void addCloseListener( final File anFile, FileHandlerDialog anDialog ) {
    anDialog.addWindowListener( new WindowAdapter(){
      @Override
      public void windowClosing( WindowEvent anE ) {
        myDialogs.remove( anFile );
      }
    });
  }

  @Override
  public void fileSaved( File aFile ) {
    myDialogs.get( aFile ).fileSaved( aFile );
  }

  @Override
  public void fileTransfer( File aFile, long aBytesReceived, long aTotalBytes ) {
    myDialogs.get(aFile).fileTransfer( aFile, aBytesReceived, aTotalBytes );
  }

  @Override
  public void fileTransferInterrupted( File aFile ) {
    myDialogs.get( aFile ).fileTransferInterrupted(  aFile );
    myDialogs.remove( aFile );
  }
}
