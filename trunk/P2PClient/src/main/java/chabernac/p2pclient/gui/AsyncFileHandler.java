/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pclient.gui;

import java.io.File;

import chabernac.protocol.asyncfiletransfer.Percentage;
import chabernac.protocol.asyncfiletransfer.iAsyncFileTransferHandler;
import chabernac.protocol.facade.P2PFacade;
import chabernac.protocol.facade.P2PFacadeException;
import chabernac.protocol.filetransfer.FileTransferException;

public class AsyncFileHandler implements iAsyncFileTransferHandler {
  private final P2PFacade myFacade;
  
  public AsyncFileHandler(P2PFacade aFacade){
    myFacade = aFacade;
  }

  @Override
  public File acceptFile( String aFileName, String aFileId ) {
    try {
      myFacade.showFileTransferOverView();
    } catch ( P2PFacadeException e ) {
    }
    return new File("c:\temp\test.dat");
//    JFileChooser theChooser = new JFileChooser();
//    int theReturn = theChooser.showOpenDialog( null );
//    if(theReturn == JFileChooser.APPROVE_OPTION){
//      return theChooser.getSelectedFile();
//    }
//    return null;
  }

  @Override
  public void fileTransfer( String aFileName, String aFileId, Percentage aPercentageComplete ) {
    // TODO Auto-generated method stub
  }

  @Override
  public void fileSaved( File aFile ) throws FileTransferException {
    // TODO Auto-generated method stub
  }

}
