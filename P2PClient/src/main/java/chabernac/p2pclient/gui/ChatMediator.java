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

import org.apache.log4j.Logger;

import chabernac.events.EventDispatcher;
import chabernac.gui.event.SavePreferencesEvent;
import chabernac.preference.ApplicationPreferences;
import chabernac.protocol.facade.P2PFacade;
import chabernac.protocol.facade.P2PFacadeException;
import chabernac.protocol.message.MessageArchive;
import chabernac.protocol.message.MultiPeerMessage;
import chabernac.protocol.message.iMultiPeerMessageListener;
import chabernac.protocol.userinfo.UserInfo.Status;
import chabernac.tools.Tools;

public class ChatMediator {
  private static Logger LOGGER = Logger.getLogger(ChatMediator.class);

  private P2PFacade myP2PFacade = null;
  private iUserSelectionProvider myUserSelectionProvider = null;
  private iMessageProvider myMessageProvider = null;
  private iReceivedMessagesProvider myReceivedMessagesProvider = null;
  private iTitleProvider myTitleProvider = null;
  private isShowDialogProvider myIsShowDialogProvider = null;
  private iMessageDialog myMessageDialog = null;

  private ExecutorService myExecutorService = Executors.newFixedThreadPool( 5 );

  private MultiPeerMessage myLastSendMessage = null;
  private MultiPeerMessage myConcept = null;
  private MessageArchive myMessagArchive = null;

  private boolean isShowDialog = true;

  private int myRestoreIndex = -1;

  public ChatMediator ( P2PFacade anFacade ) throws P2PFacadeException {
    super();
    setP2PFacade( anFacade );
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
    myP2PFacade.addMessageListener( new MyMessageListener() );
    myMessageDialog  = NewMessageDialog5.getInstance( this );
  }
  public iUserSelectionProvider getUserSelectionProvider() {
    return myUserSelectionProvider;
  }
  public void setUserSelectionProvider( iUserSelectionProvider anUserSelectionProvider ) {
    myUserSelectionProvider = anUserSelectionProvider;
    myUserSelectionProvider.addSelectionChangedListener( new MySelectionChangedListener() );
  }

  public synchronized Future< MultiPeerMessage > send(){
    if(checkForCommands()) return null;
    if(myUserSelectionProvider.getSelectedUsers().size() == 0) return null;
    myLastSendMessage = createMessage();
    Future< MultiPeerMessage > theFuture = myP2PFacade.sendEncryptedMessage( myLastSendMessage, myExecutorService );
    clear();
    myRestoreIndex = -1;
    myConcept = null;
    return theFuture;
  }

  private boolean checkForCommands() {
    if(myMessageProvider.getMessage().equalsIgnoreCase( "route print" )){
      try {
        myP2PFacade.showRoutingTable();
      } catch ( P2PFacadeException e ) {
        LOGGER.error( "Could not execute command" );
      }
      return true;
    }
    if(myMessageProvider.getMessage().equalsIgnoreCase( "exit" )){
      try{
        EventDispatcher.getInstance( SavePreferencesEvent.class ).fireEvent( new SavePreferencesEvent() );
        ApplicationPreferences.getInstance().save();
        myP2PFacade.stop();
      }catch(Throwable e){
        LOGGER.error("Could not properly exit", e);
      } finally {
        System.exit(0);
      }
    }
    return false;
  }

  public MultiPeerMessage createMessage(){
    return MultiPeerMessage.createMessage( myMessageProvider.getMessage() )
    .setDestinations( myUserSelectionProvider.getSelectedUsers() );
  }

  public MultiPeerMessage getLastSendMessage() {
    return myLastSendMessage;
  }

  public void saveConcept(){
    if(!myMessageProvider.getMessage().equals( "" )){
      myConcept = createMessage();
    }
  }

  public void restoreConcept(){
    restoreMessage( myConcept );
    if(myConcept != null){
      myMessageProvider.setMessageTitle( "Concept" );
    }
  }

  private void restoreMessage(MultiPeerMessage aMessage){
    if(aMessage != null){
      myUserSelectionProvider.setSelectedUsers( aMessage.getDestinations() );
      myUserSelectionProvider.setMultiPeerMessage( aMessage );
      myMessageProvider.setMessage( aMessage.getMessage() );
    } else {
      clearAll();
    }
    setTitle();
  }

  private void restoreMessageAtIndex(){
    List< MultiPeerMessage > theMessageList = new ArrayList< MultiPeerMessage >(myMessagArchive.getDeliveryReports().keySet());
    MultiPeerMessage theMessage = theMessageList.get( myRestoreIndex );
    myMessageProvider.setMessageTitle( theMessageList.indexOf( theMessage ) + 1 + "/" + theMessageList.size() );
    restoreMessage( theMessage );
  }

  public void restoreLastMessage(){
    myRestoreIndex = myMessagArchive.getDeliveryReports().values().size() - 1;
    restoreMessageAtIndex();
  }

  public void restoreFirstMessage(){
    myRestoreIndex = 0;
    restoreMessageAtIndex();
  }

  public void restorePreviousMessage(){
    //-1 means that a message was just send, if we go to the previous message, than that is the last message
    if(myRestoreIndex == -1){
      saveConcept();
      myRestoreIndex = myMessagArchive.getDeliveryReports().values().size() - 1;
    } else {
      myRestoreIndex--;
    }

    if(myRestoreIndex < 0 && !myMessagArchive.getDeliveryReports().isEmpty()){
      myRestoreIndex = 0;
    }

    if(myRestoreIndex >= 0){
      restoreMessageAtIndex();
    }
  }

  public void restoreNextMesssage(){
    if(myRestoreIndex != -1){
      myRestoreIndex++;
      if(myRestoreIndex >= myMessagArchive.getDeliveryReports().size()){
        myRestoreIndex = -1;
        restoreConcept();
      } else {
        restoreMessageAtIndex();
      }
    }
  }

  public void deleteCurrentMessage() {
    // TODO Auto-generated method stub
  }

  public void clearAll(){
    myUserSelectionProvider.clear();
    myMessageProvider.clear();
  }

  public void clear(){
    if("".equals( myMessageProvider.getMessage() )){
      myUserSelectionProvider.clear();
    } else {
      myMessageProvider.clear();
    }
  }

  public void selectReplyUsers(){
    if(!myMessagArchive.getReceivedMessages().isEmpty()){
      MultiPeerMessage theMessage = myMessagArchive.getReceivedMessages().get( myMessagArchive.getReceivedMessages().size() - 1 );
      theMessage = theMessage.reply();
      myUserSelectionProvider.setSelectedUsers( theMessage.getDestinations() );
    }
  }

  public void selectReplyAllUsers(){
    if(!myMessagArchive.getReceivedMessages().isEmpty()){
      MultiPeerMessage theMessage = myMessagArchive.getReceivedMessages().get( myMessagArchive.getReceivedMessages().size() - 1 );
      theMessage = theMessage.replyAll();
      myUserSelectionProvider.setSelectedUsers( theMessage.getDestinations() );
    }
  }

  public void clearReceivedMessages() {
    myMessagArchive.clear();
    myReceivedMessagesProvider.clear();
  }

  public iReceivedMessagesProvider getReceivedMessagesProvider() {
    return myReceivedMessagesProvider;
  }

  public void setReceivedMessagesProvider( iReceivedMessagesProvider anReceivedMessagesProvider ) {
    myReceivedMessagesProvider = anReceivedMessagesProvider;
  }

  public iTitleProvider getTitleProvider() {
    return myTitleProvider;
  }

  public void setTitleProvider( iTitleProvider anTitleProvider ) {
    myTitleProvider = anTitleProvider;
  }

  public void setTitle(){
    List<String> theUsers = myUserSelectionProvider.getSelectedUsers();
    String theTitle = ApplicationPreferences.getInstance().getProperty("frame.light.title","Chatterke");
    try{
      theTitle += " [" + myP2PFacade.getPersonalInfo().getStatus().name();
      if(!isShowDialog){
        theTitle += " - popup blocked";
      }
      theTitle += "]";
      if(theUsers.size() == 1){
        theTitle += " - sc " + Tools.getShortNameForUser( myP2PFacade.getUserInfo().get( theUsers.get( 0 ) )); 
      } else if(theUsers.size() > 1){
        theTitle += " - mc ";
      }
    }catch(P2PFacadeException e){
      LOGGER.error( "Could not set title", e );
    }
    myTitleProvider.setTitle( theTitle );
  }

  private class MySelectionChangedListener implements iSelectionChangedListener {

    @Override
    public void selectionChanged() {
      setTitle();
    }
  }

  public void setShowDialog( boolean isShowDialog ) {
    this.isShowDialog = isShowDialog;
    try{
      if(isShowDialog){
        myP2PFacade.getPersonalInfo().setStatus(Status.ONLINE);
      } else {
        myP2PFacade.getPersonalInfo().setStatus(Status.BUSY);
      }
    }catch(P2PFacadeException e){
      LOGGER.error("Could not change status", e);
    }
    setTitle();
  }
  
  public boolean isShowDialog(){
    return isShowDialog;
  }

  public isShowDialogProvider getIsShowDialogProvider() {
    return myIsShowDialogProvider;
  }

  public void setIsShowDialogProvider( isShowDialogProvider anIsShowDialogProvider ) {
    myIsShowDialogProvider = anIsShowDialogProvider;
  }


  private class MyMessageListener implements iMultiPeerMessageListener {

    @Override
    public void messageReceived( MultiPeerMessage aMessage ) {
      if(isShowDialog && myIsShowDialogProvider.isShowDialog()){
        myMessageDialog.showMessage( aMessage );
      }
    }
  }

  public void setLastSendMessage( MultiPeerMessage aMessage ) {
    myLastSendMessage = aMessage;
  }
}
