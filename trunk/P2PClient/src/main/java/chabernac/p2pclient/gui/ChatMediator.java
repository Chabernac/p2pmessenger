/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pclient.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import chabernac.protocol.facade.P2PFacade;
import chabernac.protocol.facade.P2PFacadeException;
import chabernac.protocol.message.MessageArchive;
import chabernac.protocol.message.MultiPeerMessage;

public class ChatMediator {
  private P2PFacade myP2PFacade = null;
  private iUserSelectionProvider myUserSelectionProvider = null;
  private iMessageProvider myMessageProvider = null;
  private ExecutorService myExecutorService = Executors.newFixedThreadPool( 5 );

  private MultiPeerMessage myLastSendMessage = null;
  private MultiPeerMessage myConcept = null;
  private MessageArchive myMessagArchive = null;

  private int myRestoreIndex = -1;
  
  

  public ChatMediator ( P2PFacade anFacade , iUserSelectionProvider anUserSelectionProvider , iMessageProvider anMessageProvider ) throws P2PFacadeException {
    super();
    setP2PFacade( anFacade );
    myUserSelectionProvider = anUserSelectionProvider;
    myMessageProvider = anMessageProvider;
  }
  
  public iMessageProvider getMessageProvider() {
    return myMessageProvider;
  }
  public void setMessageProvider( iMessageProvider anMessageProvider ) {
    myMessageProvider = anMessageProvider;
  }
  public P2PFacade getP2PFacade() {
    return myP2PFacade;
  }
  public void setP2PFacade( P2PFacade anFacade ) throws P2PFacadeException {
    myP2PFacade = anFacade;
    myMessagArchive = myP2PFacade.getMessageArchive();
  }
  public iUserSelectionProvider getUserSelectionProvider() {
    return myUserSelectionProvider;
  }
  public void setUserSelectionProvider( iUserSelectionProvider anUserSelectionProvider ) {
    myUserSelectionProvider = anUserSelectionProvider;
  }

  public synchronized Future< MultiPeerMessage > send(){
    myLastSendMessage = createMessage();
    Future< MultiPeerMessage > theFuture = myP2PFacade.sendEncryptedMessage( myLastSendMessage, myExecutorService );
    myMessageProvider.clear();
    myUserSelectionProvider.setSelectedUsers( new ArrayList< String >() );
    myRestoreIndex = -1;
    myConcept = null;
    return theFuture;
  }

  public MultiPeerMessage createMessage(){
    return MultiPeerMessage.createMessage( myMessageProvider.getMessage() )
    .setDestinations( myUserSelectionProvider.getSelectedUsers() );
  }

  public MultiPeerMessage getLastSendMessage() {
    return myLastSendMessage;
  }

  public void saveConcept(){
    myConcept = createMessage();
  }

  public void restoreConcept(){
    restoreMessage( myConcept );
  }

  private void restoreMessage(MultiPeerMessage aMessage){
    if(aMessage != null){
      myUserSelectionProvider.setSelectedUsers( aMessage.getDestinations() );
      myMessageProvider.setMessage( aMessage.getMessage() );
    } else {
      myUserSelectionProvider.setSelectedUsers( new ArrayList< String >() );
      myMessageProvider.clear();
    }
  }
  
  private void restoreMessageAtIndex(){
    List< MultiPeerMessage > theMessageList = new ArrayList< MultiPeerMessage >(myMessagArchive.getDeliveryReports().keySet());
    MultiPeerMessage theMessage = theMessageList.get( myRestoreIndex );
    restoreMessage( theMessage );
  }

  public void restorePreviousMessage(){
    //-1 means that a message was just send, if we go to the previous message, than that is the last message
    if(myRestoreIndex == -1){
      saveConcept();
      myRestoreIndex = myMessagArchive.getDeliveryReports().values().size() - 1;
    } else {
      myRestoreIndex--;
    }
    
    if(myRestoreIndex < 0){
      myRestoreIndex = 0;
    }
    
    restoreMessageAtIndex();
  }

  public void restoreNextMesssage(){
    if(myRestoreIndex != -1){
      if(myRestoreIndex >= myMessagArchive.getDeliveryReports().size()){
        myRestoreIndex = -1;
        restoreConcept();
      } else {
        restoreMessageAtIndex();
      }
    }
  }
}
