/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pclient.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.apache.log4j.Logger;

import chabernac.command.CommandSession;
import chabernac.easteregg.EasterEggFactory;
import chabernac.easteregg.iEasterEgg;
import chabernac.gui.tray.SystemTrayMenu;
import chabernac.p2pclient.gui.action.ActionFactory;
import chabernac.p2pclient.gui.action.ActionFactory.Action;
import chabernac.p2pclient.settings.Settings;
import chabernac.preference.ApplicationPreferences;
import chabernac.preference.iApplicationPreferenceListener;
import chabernac.protocol.asyncfiletransfer.iAsyncFileTransferHandler;
import chabernac.protocol.facade.P2PFacade;
import chabernac.protocol.facade.P2PFacadeException;
import chabernac.protocol.message.MessageArchive;
import chabernac.protocol.message.MessageIndicator;
import chabernac.protocol.message.MultiPeerMessage;
import chabernac.protocol.message.iMultiPeerMessageListener;
import chabernac.protocol.userinfo.UserInfo.Status;
import chabernac.thread.DynamicSizeExecutor;

public class ChatMediator {
  private static Logger LOGGER = Logger.getLogger(ChatMediator.class);

  private P2PFacade myP2PFacade = null;
  private iUserSelectionProvider myUserSelectionProvider = null;
  private iMessageProvider myMessageProvider = null;
  private iAttachementProvider myAttachementProvider = null;
  private iReceivedMessagesProvider myReceivedMessagesProvider = null;
  private iTitleProvider myTitleProvider = null;
  private isShowDialogProvider myIsShowDialogProvider = null;
  private iMessageDialog myMessageDialog = null;
  private iChatFrame myChatFrame = null;
  private SystemTrayMenu mySystemTrayMenu = null;
  private iAsyncFileTransferHandler myFileHandler = null;

  private ExecutorService myExecutorService = DynamicSizeExecutor.getTinyInstance();

  private MultiPeerMessage myLastSendMessage = null;
  private MultiPeerMessage myConcept = null;
  private MessageArchive myMessagArchive = null;

  private int myRestoreIndex = -1;

  private ExecutorService myFileTransferr = DynamicSizeExecutor.getTinyInstance();
  private ExecutorService myFileTransferResponse = DynamicSizeExecutor.getTinyInstance();
  
  private final ActionFactory myActionFactory;
  private final String POPUP_BLOCKED_MESSAGE = "Let op: popups zijn geblokkeerd!";

  public ChatMediator ( P2PFacade anFacade ) throws P2PFacadeException {
    super();
    myActionFactory = new ActionFactory(this);
    setP2PFacade( anFacade );
    addPreferenceListener();
    //TODO does this belong here?
    setupFileHandler();
  }
  
  private void setupFileHandler() throws P2PFacadeException{
    myFileHandler = new AsyncFileHandler(this);
    myP2PFacade.setAsyncFileHandler( myFileHandler );
  }
  
  private void addPreferenceListener(){
    ApplicationPreferences.getInstance().addApplicationPreferenceListener( new MyPreferenceListener() );
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
//    myUserSelectionProvider.addSelectionChangedListener( new MySelectionChangedListener() );
  }
  
  public iChatFrame getChatFrame() {
    return myChatFrame;
  }

  public void setChatFrame( iChatFrame myFrameProvider ) {
    this.myChatFrame = myFrameProvider;
  }
  
  public SystemTrayMenu getSystemTrayMenu() {
    return mySystemTrayMenu;
  }

  public void setSystemTrayMenu( SystemTrayMenu aSystemTrayMenu ) {
    mySystemTrayMenu = aSystemTrayMenu;
  }

  public synchronized Future< MultiPeerMessage > send(){
    if(checkForCommands()) return null;
//    sendSystemMessage( "test" );
    if(myUserSelectionProvider.getSelectedUsers().size() == 0) return null;
    if(handleAttachements()) return null;
    if("".equals( myMessageProvider.getMessage())) return null;
    myLastSendMessage = createMessage();
    Future< MultiPeerMessage > theFuture = myP2PFacade.sendEncryptedMessage( myLastSendMessage, myExecutorService );
    clear();
    myRestoreIndex = -1;
    myConcept = null;
    return theFuture;
  }

  private boolean handleAttachements(){
    boolean isFile = false;
    if(myAttachementProvider.getAttachments() != null && myAttachementProvider.getAttachments().size() > 0){
      isFile = true;
      for(String theUser : myUserSelectionProvider.getSelectedUsers()){
        for(File theFile : myAttachementProvider.getAttachments()){
          myFileTransferResponse.execute( new  MyFileTransferResponseDisplayer(myP2PFacade.sendFile( theFile, theUser, myFileTransferr),
                                                                               theFile, 
                                                                               theUser));
        }
      }
    }
    return isFile;
  }

  private boolean checkForCommands() {
    if(myMessageProvider.getMessage().equalsIgnoreCase( "route print" )){
      try {
        myP2PFacade.showRoutingTable();
      } catch ( P2PFacadeException e ) {
        LOGGER.error( "Could not execute command" );
      }
      return true;
    } else if(myMessageProvider.getMessage().equalsIgnoreCase( "exit" )){
      CommandSession.getInstance().execute(myActionFactory.getCommand(Action.EXIT_WITHOUT_ASKING));
    }
    
    checkEasterEgg( myMessageProvider.getMessage() );
    
    return false;
  }

  public MultiPeerMessage createMessage(){
    MultiPeerMessage theMessage = MultiPeerMessage.createMessage( myMessageProvider.getMessage() )
    .setDestinations( myUserSelectionProvider.getSelectedUsers() );
    if(myMessageProvider.isSendClosed()){
      theMessage = theMessage.addMessageIndicator(MessageIndicator.CLOSED_ENVELOPPE);
    }
    return theMessage;
    
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
      myMessageProvider.setSendClosed( aMessage.getIndicators().contains( MessageIndicator.CLOSED_ENVELOPPE ) );
    } else {
      clearAll();
    }
//    setTitle();
  }

  public void restoreMessageAtIndex(int anIndex){
    myRestoreIndex = anIndex;
    List< MultiPeerMessage > theMessageList = new ArrayList< MultiPeerMessage >(myMessagArchive.getDeliveryReports().keySet());
    MultiPeerMessage theMessage = theMessageList.get( myRestoreIndex );
    myMessageProvider.setMessageTitle( theMessageList.indexOf( theMessage ) + 1 + "/" + theMessageList.size() );
    restoreMessage( theMessage );
  }

  public void restoreLastMessage(){
    restoreMessageAtIndex(myMessagArchive.getDeliveryReports().values().size() - 1);
  }

  public void restoreFirstMessage(){
    restoreMessageAtIndex(0);
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
      restoreMessageAtIndex(myRestoreIndex);
    }
  }

  public void restoreNextMesssage(){
    if(myRestoreIndex != -1){
      myRestoreIndex++;
      if(myRestoreIndex >= myMessagArchive.getDeliveryReports().size()){
        myRestoreIndex = -1;
        restoreConcept();
      } else {
        restoreMessageAtIndex(myRestoreIndex);
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

//  public void setTitle(){
//    if(true) return;
//    Set<String> theUsers = myUserSelectionProvider.getSelectedUsers();
//    String theTitle = ApplicationPreferences.getInstance().getProperty("frame.light.title","Chatterke");
//    try{
//      theTitle += " [" + myP2PFacade.getPersonalInfo().getStatus().name();
//      if(myP2PFacade.getPersonalInfo().getStatusMessage() != null && !myP2PFacade.getPersonalInfo().getStatusMessage().equals( "" )){
//        theTitle += " " + myP2PFacade.getPersonalInfo().getStatusMessage();
//      }
//      if(ApplicationPreferences.getInstance().hasEnumProperty( Settings.ReceiveEnveloppe.NO_POPUP )){
//        theTitle += " - popup geblokkeerd";
//      } else if(ApplicationPreferences.getInstance().hasEnumProperty( Settings.ReceiveEnveloppe.CLOSED )){
//        theTitle += " - ontvang gesloten";
//      }
//      theTitle += "]";
//      if(theUsers.size() == 1){
//        theTitle += " - sc " + Tools.getShortNameForUser( myP2PFacade.getUserInfo().get( theUsers.iterator().next() )); 
//      } else if(theUsers.size() > 1){
//        theTitle += " - mc ";
//      }
//    }catch(P2PFacadeException e){
//      LOGGER.error( "Could not set title", e );
//    }
//    myTitleProvider.setTitle( theTitle );
//  }
  
  public void setPopupMessage(){
    if(myMessageProvider instanceof JComponent){
      if(ApplicationPreferences.getInstance().hasEnumProperty( Settings.ReceiveEnveloppe.NO_POPUP )){
        myMessageProvider.setMessage( POPUP_BLOCKED_MESSAGE );
      } else {
        if(myMessageProvider.getMessage().equalsIgnoreCase( POPUP_BLOCKED_MESSAGE )){
          myMessageProvider.clear();
        }
      }
    }
  }

//  private class MySelectionChangedListener implements iSelectionChangedListener {
//
//    @Override
//    public void selectionChanged() {
//      setTitle();
//    }
//  }

  private void checkReceiveStatus( ) {
    try{
      if(ApplicationPreferences.getInstance().hasEnumProperty( Settings.ReceiveEnveloppe.NO_POPUP )){
        myP2PFacade.getPersonalInfo().setStatus(Status.BUSY);
      } else {
        myP2PFacade.getPersonalInfo().setStatus(Status.ONLINE);
      }
    }catch(P2PFacadeException e){
      LOGGER.error("Could not change status", e);
    }
//    setTitle();
    setPopupMessage();
  }

  public isShowDialogProvider getIsShowDialogProvider() {
    return myIsShowDialogProvider;
  }

  public void setIsShowDialogProvider( isShowDialogProvider anIsShowDialogProvider ) {
    myIsShowDialogProvider = anIsShowDialogProvider;
  }

  public ActionFactory getActionFactory() {
    return myActionFactory;
  }

  private class MyMessageListener implements iMultiPeerMessageListener {

    @Override
    public void messageReceived( MultiPeerMessage aMessage ) {
      if(!checkEasterEgg(aMessage.getMessage()) && 
          !ApplicationPreferences.getInstance().hasEnumProperty( Settings.ReceiveEnveloppe.NO_POPUP ) && 
          !ApplicationPreferences.getInstance().hasEnumProperty( Settings.ReceiveEnveloppe.INFO_PANEL) &&
          myIsShowDialogProvider.isShowDialog()){
        myMessageDialog.showMessage( aMessage );
      }
    }
  }
  
  private boolean checkEasterEgg(String aMessage){
    if(aMessage.startsWith( "easteregg" ) && myChatFrame instanceof JFrame){
      iEasterEgg theEgg = EasterEggFactory.createEasterEgg( (JFrame)myChatFrame, aMessage.substring( aMessage.indexOf( " " ) ));
      theEgg.start();
      return true;
    }
    return false;
  }

  public void setLastSendMessage( MultiPeerMessage aMessage ) {
    myLastSendMessage = aMessage;
  }

  public iAttachementProvider getAttachementProvider() {
    return myAttachementProvider;
  }

  public void setAttachementProvider( iAttachementProvider anAttachementProvider ) {
    myAttachementProvider = anAttachementProvider;
  }

  private class MyFileTransferResponseDisplayer implements Runnable{
    private final Future< Boolean > myResult;
    private final File myFile;
    private final String myPeer;

    public MyFileTransferResponseDisplayer ( Future< Boolean > anResult, File aFile, String aPeer ) {
      super();
      myResult = anResult;
      myFile = aFile;
      myPeer = aPeer;
    }

    @Override
    public void run() {
      boolean isAccepted = false;
      try{
        isAccepted = myResult.get();
      }catch(Exception e){
      }
      String theUserName = myPeer;
      try{
        if(myP2PFacade.getUserInfo().containsKey( myPeer )){
          theUserName = myP2PFacade.getUserInfo().get( myPeer ).getName();
        }
      }catch(Exception e){
        LOGGER.error("Could not get username for peer '" + myPeer + "'");
      }
      sendSystemMessage( myFile.toString() + " was " + (isAccepted ? "" : " NOT ") + " accepted by peer '" + theUserName + "'" );
    }

  }

  public void sendSystemMessage(String aMessage){
    try{
      MultiPeerMessage theMessage = MultiPeerMessage.createMessage( aMessage )
      .setSource( "SYSTEM" )
      .addDestination( myP2PFacade.getPeerId() )
      .setLoopBack( true );
      myP2PFacade.sendMessage( theMessage, myExecutorService );    
    }catch(Exception e){
      LOGGER.error( "Could not send system message", e );
    }
  }
  
  public class MyPreferenceListener implements iApplicationPreferenceListener {
    @Override
    public void applicationPreferenceChanged( String aKey, String aValue ) {

    }

    @Override
    public void applicationPreferenceChanged( Enum anEnumValue ) {
      if(anEnumValue instanceof Settings.ReceiveEnveloppe){
        checkReceiveStatus();
      }
    }
  }
}
