/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pclient.gui;

import org.apache.log4j.Logger;

import chabernac.protocol.facade.P2PFacadeException;
import chabernac.protocol.packet.AbstractTransferState;
import chabernac.protocol.packet.AbstractTransferState.Direction;
import chabernac.protocol.packet.AbstractTransferState.Side;
import chabernac.protocol.packet.AudioTransferState;
import chabernac.protocol.packet.FileTransferState;
import chabernac.protocol.packet.iTransferListener;
import chabernac.protocol.userinfo.UserInfo;

public class AsyncTransferHandler implements iTransferListener {
  private static Logger LOGGER = Logger.getLogger(AsyncTransferHandler.class);
  private final ChatMediator myMediator;

  public AsyncTransferHandler(ChatMediator aMediator){
    myMediator = aMediator;
  }

  @Override
  public void newTransfer( AbstractTransferState aTransfer) {
    //only show information when we are the receiving side
    if(aTransfer.getSide() != Side.RECEIVER) return;
    
    if(aTransfer instanceof FileTransferState && aTransfer.getDirection() == Direction.RECEIVE){
      FileTransferState theFileTransfer = ((FileTransferState)aTransfer);
    
    try{
      UserInfo theUserInfo = myMediator.getP2PFacade().getUserInfo().get(aTransfer.getRemotePeer());
      String theFrom = aTransfer.getRemotePeer();
      if(theUserInfo != null){
        theFrom = theUserInfo.getName();
      }

      myMediator.sendSystemMessage( aTransfer.getRemotePeer(), theFrom + " wenst u een file te sturen, klik op <a href='download:" + aTransfer.getTransferId() + ":" + theFileTransfer.getTransferDescription()+ "'>" + theFileTransfer.getFile().getName() + "</a> om de file te ontvangen" );
    }catch(P2PFacadeException e){
      LOGGER.error( "An error occured while sending system message", e );
    }
    } else if(aTransfer instanceof AudioTransferState){
      AudioTransferState theAudioTransfer = ((AudioTransferState)aTransfer);
      try{
        UserInfo theUserInfo = myMediator.getP2PFacade().getUserInfo().get(aTransfer.getRemotePeer());
        String theFrom = aTransfer.getRemotePeer();
        if(theUserInfo != null){
          theFrom = theUserInfo.getName();
        }

        myMediator.sendSystemMessage( aTransfer.getRemotePeer(), theFrom + " wenst met u te bellen, klik op <a href='audio:" + aTransfer.getTransferId() + "'>" + theAudioTransfer.getTransferDescription() + "</a> om de audio stream te starten" );
      }catch(P2PFacadeException e){
        LOGGER.error( "An error occured while sending system message", e );
      }
    }
  }

  @Override
  public void transferRemoved( AbstractTransferState aTransfer ) {
    // TODO Auto-generated method stub
  }
}
