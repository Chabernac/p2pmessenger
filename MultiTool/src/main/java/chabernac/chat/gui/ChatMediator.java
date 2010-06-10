/*
 * Created on 17-feb-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.chat.gui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;

import chabernac.chat.Message;
import chabernac.chat.UserList;
import chabernac.chat.gui.event.ChangeUserStatusEvent;
import chabernac.chat.gui.event.ShowChatPanelEvent;
import chabernac.easteregg.EasterEggFactory;
import chabernac.easteregg.iEasterEgg;
import chabernac.easteregg.iEasterEggListener;
import chabernac.event.ApplicationEventDispatcher;
import chabernac.event.Event;
import chabernac.event.iEventListener;
import chabernac.messengerservice.MessageArchive;
import chabernac.messengerservice.MessengerClientService;
import chabernac.messengerservice.MessengerUser;
import chabernac.messengerservice.event.MessageReceivedEvent;
import chabernac.util.StatusDispatcher;
import chabernac.util.Tools;

public abstract class ChatMediator implements iEventListener {
  private static Logger logger = Logger.getLogger(ChatMediator.class);
  private static final String EASTEREGG_TRIGGER = "easteregg";

  private MessengerClientService myMessengerClientService = null;
  private MessageArchive myMessageArchive = null;
  private UserListPanel userListPanel = null;
  private UserList userList = null;
  private MessageField messageField = null;
  private ReceivedMessagesField receivedField = null;
  private JFrame mainFrame = null;
  private JPanel myChatPanel = null;
  private ChatGUIBuilder myGUIBuilder = null;
  private String mainFrameTitle = null;
  private boolean showDialog = true;
  private iEasterEgg myEasteregg = null;

  public ChatMediator(MessengerClientService aModel, JFrame aFrame, ChatGUIBuilder aBuilder){
    myMessengerClientService = aModel;
    mainFrame = aFrame;
    mainFrameTitle = mainFrame.getTitle();
    myGUIBuilder = aBuilder;
    //by adding the lister before initializing the gui components this listener will always be notifief as latest
    //euh? --> see techical implementation of the Observable class
    addListener();
    init();
    buildActionMaps();
    ApplicationEventDispatcher.addListener(this, new Class[]{ShowChatPanelEvent.class});

  }

  private void init(){
    myMessageArchive = new MessageArchive(myMessengerClientService);
    userList = new UserList(myMessengerClientService);
    userListPanel = myGUIBuilder.buildUserListPanel(this);
    userListPanel.addObserver(new SelectedUsersObserver());
    messageField = myGUIBuilder.buildMessageField(this);
    receivedField = myGUIBuilder.buildReceivedMessagesField(this);
    myChatPanel = myGUIBuilder.buildChatPanel(this);
    NewMessageDialog4.creatIntance(this);
  }

  private void buildActionMaps(){
    InputMap theMap = myChatPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    for(char theChar=33;theChar<=126;theChar++){
      if(!(theChar == '+' || theChar == '-')){
        theMap.put(KeyStroke.getKeyStroke(theChar), "focusinput");
      }
    }
    theMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAUSE, 0), "nodialog");

    ActionMap theActionMap = myChatPanel.getActionMap();
    theActionMap.put("focusinput", new FocusInputAction());
    theActionMap.put("nodialog", new NoDialogAction());


    InputMap theMessageMap = messageField.getInputMap();
    KeyStroke[] theStrokes = theMessageMap.allKeys();
    for(int i=0;i<theStrokes.length;i++){
      theMap.put(theStrokes[i], theMessageMap.get(theStrokes[i]));
    }

    ActionMap theMessageActionMap = messageField.getActionMap();
    Object[] theKeys = theMessageActionMap.allKeys();
    for(int i=0;i<theKeys.length;i++){
      theActionMap.put(theKeys[i], theMessageActionMap.get(theKeys[i]));
    }

    getReceivedMessagesField().getPane().setInputMap(JComponent.WHEN_FOCUSED, theMap);
    getReceivedMessagesField().getPane().setActionMap(theActionMap);

    getMessageField().addKeyListener(new KeyAdapter(){
      public void keyPressed(KeyEvent e) {
        userListPanel.clearStatuses();
      }
    });
  }

  private void addListener(){
    ApplicationEventDispatcher.addListener(new MessageReceivedEventListener(), MessageReceivedEvent.class);
    //myMessengerClientService.addObserver(new MessageObserver());
  }

  public MessengerClientService getMessengerClientService(){
    return myMessengerClientService;
  }

  public MessageArchive getMessageArchive(){
    return myMessageArchive;
  }

  public UserList getUserList(){
    return userList;
  }

  public UserListPanel getUserListPanel(){
    return userListPanel;
  }

  public MessageField getMessageField(){
    return messageField;
  }

  public ReceivedMessagesField getReceivedMessagesField(){
    return receivedField;
  }

  public JFrame getMainFrame(){
    return mainFrame;
  }

  public JPanel getChatPanel(){
    return myChatPanel;
  }



  private Message prepareMessage() throws RemoteException{
    Message theMessage = null;
    if(myMessageArchive.getSelectedMessage() != null && !myMessageArchive.getSelectedMessage().isSend()){
      theMessage = myMessageArchive.getSelectedMessage();
    } else {
      theMessage = new Message();
    }
    if(!theMessage.isSend()){
      theMessage.setFrom(myMessengerClientService.getUser().getId());
      theMessage.setTo(userListPanel.getSelectedUsers());
      theMessage.setMessage(messageField.getText());
      ArrayList theAttachments = messageField.getAttachments();
      if(theAttachments != null){
        for(int i=0;i<theAttachments.size();i++) {
          theMessage.addAttachment((Serializable)theAttachments.get(i));
        }
      }
    }
    return theMessage;
  }

  public void send() throws RemoteException{
    if(messageField.getText().equals("")) return;
    if(userListPanel.getSelectedUsers().isEmpty()){
      StatusDispatcher.showWarning("No users were selected");
      return;
    }
    Message theMessage = prepareMessage();


    if(theMessage.getMessage().startsWith("sleep")) {
      try {
        Thread.sleep(3000);
      } catch (InterruptedException e) {
        logger.error("An error occured during sleeping", e);
      }
    }


    //theMessage.addObserver(new MessageStatusObserver());
    myMessengerClientService.sendMessage(theMessage);
  }

  public void save() {
    if(messageField.getText().equals("")) return;
    try{
      Message theMessage = prepareMessage();
      myMessageArchive.save(theMessage);
    }catch(RemoteException e){
      logger.error("An error occured while saving message", e);
    }
  }



  private class MessageReceivedEventListener implements iEventListener{

    private void bringToFront(){
      mainFrame.setVisible(true);
      Tools.requestFocus(mainFrame);
      mainFrame.setState(JFrame.NORMAL);
      mainFrame.toFront();
      logger.debug("Focus received: " + mainFrame.requestFocusInWindow());
    }

    public void eventFired(Event anEvt) {

      final Message theMessage = ((MessageReceivedEvent)anEvt).getMessage();
      if(theMessage != null){
        if(!theMessage.isTechnicalMessage()){

          if(theMessage.getMessage().startsWith(EASTEREGG_TRIGGER)){
            NewMessageDialog4.clearMessages();
            bringToFront();

            if(myEasteregg != null && myEasteregg.isRunning()){
              myEasteregg.stop();
            }

            try{
              myEasteregg = EasterEggFactory.createEasterEgg(ChatMediator.this, theMessage.getMessage().substring(EASTEREGG_TRIGGER.length()));
              myEasteregg.setEasterEggListener( new EasterEggListener() );
              myEasteregg.start();
            }catch(Throwable e){
              StringWriter theStringWriter = new StringWriter();
              PrintWriter theWriter = new PrintWriter(theStringWriter);
              e.printStackTrace(theWriter);
              String theError = theStringWriter.getBuffer().toString();
              try{
                Message theNewMessage = theMessage.reply(getMessengerClientService().getUser().getUserName());
                theNewMessage.setMessage(theError);
                getMessengerClientService().sendMessage(theNewMessage);
              }catch(RemoteException f){}
            }

            /*
            String theEasterEgg = "";
            String theEnvironment = "";

            StringTokenizer theTokenizer = new StringTokenizer(theMessage.getMessage());
            theTokenizer.nextToken();
            if(theTokenizer.hasMoreTokens()) theEasterEgg = theTokenizer.nextToken();
            if(theTokenizer.hasMoreTokens()) theEnvironment = theTokenizer.nextToken();

            iPaintable theEasterEggPaintable = EasterEggFactory.createEasterEgg(ChatMediator.this, theEasterEgg);

            if(theEnvironment.equalsIgnoreCase("fullscreen")){
              new FullScreenPaintable(theEasterEggPaintable);
            } else {
              new EasterEggPanel(ChatMediator.this, theEasterEggPaintable);
            }
             */
          } else {

            try{
              if( (!mainFrame.isActive() || !messageField.isFocusOwner()) && !theMessage.getFrom().equals(myMessengerClientService.getUser().getUserName()) && isShowDialog()){
                System.out.println("Message event received");
                NewMessageDialog4.showDialog( theMessage );
              }
            }catch(RemoteException e){
              logger.error("An error occured while creating dialog", e);
            }
          }
        } else {
          if(theMessage.getMessage().equals("activate")){
            bringToFront();
          } 
        }
      }
    }
  }

  /*
  private class MessageStatusObserver implements Observer{
    public void update(Observable o, Object arg) {
      Message theMessage = (Message)((DefaultObservable)o).getTarget();
      //All messages will trigger state events, we ignore all of them accept for the ones of the latest message
      if(myMessengerClientService.getLastMessageSend() == theMessage){
        userListPanel.checkUsers(theMessage);
      }
    }
  }
   */

  protected void focusOnFrame(JFrame aFrame){}

  public void setTitle(){
    String title = mainFrameTitle;
    ArrayList theUsers = userListPanel.getSelectedUsers();
    if(theUsers.size() == 0) {
      title += "";
    } else if(theUsers.size() == 1) {
      MessengerUser theUser = myMessengerClientService.getUser((String)theUsers.get(0));
      if(theUser == null){
        logger.equals("The user: " + theUsers.get(0) + " does not exist in the client service, impossible!");
      } else {
        title += " - sc - " + theUser.getShortName();
      }
    } else if(theUsers.size() > 1) {
      title += " - mc";
    }
    title += " ";
    if(!isShowDialog()) title += " - popup geblokkeerd";
    mainFrame.setTitle(title);
  }

  private class SelectedUsersObserver implements Observer{
    public synchronized void update(Observable o, Object arg) {
      setTitle(); 
    }
  }

  private class FocusInputAction extends AbstractAction{

    public void actionPerformed(ActionEvent e) {
      if(!getMessageField().isFocusOwner()){
        getMessageField().setText(getMessageField().getText() + e.getActionCommand());
        getMessageField().requestFocus();
      }
    }
  }

  private class NoDialogAction extends AbstractAction{
    public void actionPerformed(ActionEvent e) {
      setShowDialog(!isShowDialog());
    }
  }

  public boolean isShowDialog() {
    return showDialog;
  }

  public void setShowDialog(boolean showDialog) {
    ApplicationEventDispatcher.fireEvent(new ChangeUserStatusEvent(showDialog ? MessengerUser.ONLINE : MessengerUser.BUSSY));
    this.showDialog = showDialog;
    setTitle();
  }

  public void eventFired(Event evt){
    if(evt instanceof ShowChatPanelEvent){
      mainFrame.setVisible(true);
      mainFrame.setState(JFrame.NORMAL);
      mainFrame.toFront();
      focusOnFrame(mainFrame);
      messageField.requestFocus();
    }
  }

  private class EasterEggListener implements iEasterEggListener{

    public void easterEggStopped() {
      myEasteregg = null;
    }
    
  }

}
