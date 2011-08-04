/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pclient.gui;

import java.io.File;

import org.apache.log4j.Logger;

import chabernac.protocol.asyncfiletransfer.AcceptFileResponse;
import chabernac.protocol.asyncfiletransfer.Percentage;
import chabernac.protocol.asyncfiletransfer.iAsyncFileTransferHandler;
import chabernac.protocol.asyncfiletransfer.iTransferController;
import chabernac.protocol.asyncfiletransfer.AcceptFileResponse.Response;
import chabernac.protocol.facade.P2PFacadeException;
import chabernac.protocol.filetransfer.FileTransferException;
import chabernac.protocol.userinfo.UserInfo;

public class AsyncFileHandler implements iAsyncFileTransferHandler {
  private static Logger LOGGER = Logger.getLogger(AsyncFileHandler.class);
  private final ChatMediator myMediator;

  public AsyncFileHandler(ChatMediator aMediator){
    myMediator = aMediator;
  }

  @Override
  public AcceptFileResponse acceptFile( String aPeerId, String aFileName, String aTransferId, iTransferController aController ) {
    try{
      UserInfo theUserInfo = myMediator.getP2PFacade().getUserInfo().get(aPeerId);
      String theFrom = aPeerId;
      if(theUserInfo != null){
        theFrom = theUserInfo.getName();
      }

      myMediator.sendSystemMessage( theFrom + " wenst u een file te sturen, klik op <a href='download:" + aTransferId + "'>downloaden</a> om de file te ontvangen" );
    }catch(P2PFacadeException e){
      LOGGER.error( "An error occured while sending system message", e );
    }
    return new AcceptFileResponse(aTransferId, Response.PENDING, null);
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
