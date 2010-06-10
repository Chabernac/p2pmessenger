package chabernac.chat.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ComponentInputMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import chabernac.chat.Message;
import chabernac.chat.gui.event.SelectUsersEvent;
import chabernac.event.ApplicationEventDispatcher;
import chabernac.messengerservice.MessengerClientService;
import chabernac.util.Tools;


public class NewMessageDialog extends JDialog{
  private static Logger logger = Logger.getLogger(NewMessageDialog.class);

  private JTextArea myText = null;
  private JTextArea myReplyText = null;
  private TitledBorder myBorder = null;
  private TitledBorder myReplyBorder = null;
  private boolean read = true;
  private MessengerClientService myChatModel = null;
  private ChatMediator myMediator = null;
  private Message myMessage = null;
  private Message myReplyMessage = null;
  private JPanel myContentPanel = null;
  private JPanel myWestPanel =  null;
  private InputMap myInputMap1 = null;
  private InputMap myInputMap2 = null;
  private JScrollPane myReplyScrollPane = null;
  private FocusWindowThread myFocusWindowThread = null;
  private boolean closing = false;

  private static NewMessageDialog instance = null;

  private NewMessageDialog(ChatMediator aMediator){
    myMediator = aMediator;
    myChatModel = myMediator.getMessengerClientService();
    init();
    buildGUI();
    initActionMap();
    addWindowListener();
  }

  private void init(){
    myText = new JTextArea();
    myText.setEditable(false);
    myText.setLineWrap(true);
    myText.setWrapStyleWord(true);
    myText.setRows(4);
    myText.setColumns(20);
//  myText.setToolTipText("r = reply, a = reply all, c = chat");
    myReplyText = new JTextArea();
    myReplyText.setLineWrap(true);
    myReplyText.setWrapStyleWord(true);
    myReplyText.setRows(4);
    myReplyText.setColumns(20);
    myReplyBorder = new TitledBorder("");
    myReplyScrollPane = new JScrollPane(myReplyText, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    myReplyScrollPane.setBorder(myReplyBorder);
    myBorder = new TitledBorder("");
//  mySystemEventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
    myFocusWindowThread = new FocusWindowThread(this, 30000);
  }

  private void addWindowListener(){
    WindowAdapter theAdapter= new WindowAdapter(){
      public void windowClosing(WindowEvent evt){
        sendOrClose();
      }
      public void windowLostFocus(WindowEvent e) {
        sendOrClose();
      }
    };
    addWindowFocusListener(theAdapter);
    addWindowListener(theAdapter);
  }

  private void initActionMap(){
    myInputMap1 = new ComponentInputMap(myContentPanel);
    //InputMap theMap = myContentPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    myInputMap1.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "reply");
    myInputMap1.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), "replyall");
    myInputMap1.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, 0), "chat");
    myInputMap1.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "sendorclose");
    myInputMap1.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "sendorclose");
    myInputMap1.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAUSE, 0), "nodialog");

    myInputMap2 = new ComponentInputMap(myContentPanel);
    myInputMap2.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "normalmode");
    myInputMap2.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "sendorclose");

    myContentPanel.setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, myInputMap1);

    ActionMap theActionMap = myContentPanel.getActionMap();
    theActionMap.put("reply", new ReplyAction());
    theActionMap.put("replyall", new ReplyAllAction());
    theActionMap.put("chat", new ChatAction());
    theActionMap.put("sendorclose", new SendOrCloseAction());
    theActionMap.put("normalmode", new NormalModeAction());
    theActionMap.put("nodialog", new NoDialogAction());


    InputMap theTextMap = myReplyText.getInputMap(JComponent.WHEN_FOCUSED);
    theTextMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "sendorclose");
    ActionMap theTextActionMap = myReplyText.getActionMap();
    theTextActionMap.put("sendorclose", new SendOrCloseAction());
  }

  private void buildGUI(){
    setTitle("Nieuw bericht");
    getContentPane().setLayout(new BorderLayout());

    myContentPanel = new JPanel(new BorderLayout());

    myWestPanel = new JPanel(new GridLayout(-1,1));
    JScrollPane thePane = new JScrollPane(myText, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    thePane.setBorder(myBorder);
    myWestPanel.add(thePane);
    myContentPanel.add(myWestPanel, BorderLayout.CENTER);

    getContentPane().add(myContentPanel, BorderLayout.CENTER);

    Toolkit theToolkit = Toolkit.getDefaultToolkit();
    pack();
    Dimension theDimension = theToolkit.getScreenSize();
    setLocation((int)(theDimension.getWidth() / 2 - getWidth() /2),(int)(theDimension.getHeight() / 2 - getHeight() / 2));
  }

  private void setMessage(Message aMessage){
    myBorder.setTitle(Tools.getEnvelop(myMediator.getUserList(), aMessage));
    myText.setText(aMessage.getMessage());
    myText.setEditable(false);
    myText.requestFocus();
    myMessage = aMessage;
    myContentPanel.setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, myInputMap1);
//  myContentPanel.requestFocus();
    pack();
    repaint();
  }

  private boolean isRead(){
    return read;
  }

  public static void creatIntance(ChatMediator aMediator){
    if(instance == null) instance = new NewMessageDialog(aMediator);
  }

  private synchronized boolean showMessage(Message aMessage){
    synchronized(aMessage){
      closing = false;
      setMessage(aMessage);
      setVisible(true);
      myFocusWindowThread.startThread();
      try {
        myMessage.wait();
      } catch (InterruptedException e) {
        logger.error("Could not wait", e);
      }
      myFocusWindowThread.stopThread();
      setVisible(false);
      Thread.yield();
    }
    return isRead();
  }

  public static boolean showDialog(Message aMessage){
    if(instance == null) return true;
    return instance.showMessage(aMessage);
  }

  private void closeDialog(boolean chat){
    if(!closing){
      synchronized (myMessage) {
        read = chat;
        myReplyText.setText("");
        myReplyMessage = null;
        addRemoveReply(false);
        myMessage.notifyAll();
      }
    }
  }

  private void sendOrClose(){

    if(myReplyMessage != null && !myReplyText.getText().equals("")){
      myReplyMessage.setMessage(myReplyText.getText());
      try {
        logger.debug("Sending message from dialog");
        myChatModel.sendMessage(myReplyMessage);
      } catch (RemoteException e) {
        logger.error("An error occured while sending message", e);
      }
    }
    closeDialog(false);
  }

  private void addRemoveReply(boolean reply){
    if(reply){
      myWestPanel.add(myReplyScrollPane, BorderLayout.WEST);
      //myText.setText(myReplyMessage.getMessage());
      //myText.setEditable(true);
      //myText.requestFocus();
      //myBorder.setTitle(myReplyMessage.getEnvelop());
      myContentPanel.setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, myInputMap2);
      myReplyBorder.setTitle(Tools.getEnvelop(myMediator.getUserList(), myReplyMessage));

//    AWTEvent theEvent = mySystemEventQueue.peekEvent();
//    if(theEvent instanceof KeyEvent){
//    KeyEvent theKeyEvent = (KeyEvent)theEvent;
//    if(theKeyEvent.getKeyChar() == 'r' || theKeyEvent.getKeyChar() == 'a'){
//    try{
//    theEvent = mySystemEventQueue.getNextEvent();
//    }catch(InterruptedException e){
//    Logger.log(this,"Error while getting event from event queue", e);
//    }
//    }
//    }
      myReplyText.requestFocus();
    } else {
      myWestPanel.remove(myReplyScrollPane);
      myContentPanel.setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, myInputMap1);
      //myText.setText(myMessage.getMessage());
      //myText.setEditable(false);
      myBorder.setTitle(Tools.getEnvelop(myMediator.getUserList(), myMessage));
      myText.requestFocus();
      myReplyMessage = null;
    }
    pack();
  }

  private class ReplyAction extends AbstractAction{
    public void actionPerformed(ActionEvent e) {
      try{
        myReplyMessage = myMessage.reply(myChatModel.getUser().getUserName());
        ApplicationEventDispatcher.fireEvent(new SelectUsersEvent(myReplyMessage.getTo()));
        addRemoveReply(true);
      }catch(RemoteException e1){
        logger.error("An error occured in ReplyAction", e1);
      }
    }
  }

  private class ReplyAllAction extends AbstractAction{
    public void actionPerformed(ActionEvent e) {
      try{
        myReplyMessage = myMessage.replyAll(myChatModel.getUser().getUserName());
        ApplicationEventDispatcher.fireEvent(new SelectUsersEvent(myReplyMessage.getTo()));
        addRemoveReply(true);
      }catch(RemoteException e1){
        logger.error("An error occured in ReplyAllAction", e1);
      }
    }
  }

  private class SendOrCloseAction extends AbstractAction{
    public void actionPerformed(ActionEvent e) {
      sendOrClose();
    }
  }

  private class ChatAction extends AbstractAction{
    public void actionPerformed(ActionEvent e) {
      closeDialog(true);
    }
  }

  private class NormalModeAction extends AbstractAction{
    public void actionPerformed(ActionEvent e) {
      addRemoveReply(false);
    }
  }

  private class NoDialogAction extends AbstractAction{
    public void actionPerformed(ActionEvent e) {
      myMediator.setShowDialog(false);
      sendOrClose();
    }
  }

  private class FocusWindowThread implements Runnable{
    private Window myWindow = null;
    private boolean isRunning = false;
    private boolean stop = false;
    private long myTimeout;

    public FocusWindowThread(Window aWindow, long aTimeout){
      myWindow = aWindow;
      myTimeout = aTimeout;
    }

    public synchronized void startThread(){
      focus();
      stop = false;
      if(isRunning) return;
      //logger.debug("Starting new thread");
      new Thread(this).start();
    }

    public synchronized void stopThread(){
      stop = true;
    }

    private void focus(){
      //logger.debug("Executing focus command");
      Tools.requestFocus(myWindow);
      myWindow.toFront();
    }

    public void run(){
      isRunning = true;
      try{
        while(!stop){
          //logger.debug("Trying to get the popup in front");
          focus();
          Thread.sleep(myTimeout);
        }
      }catch(InterruptedException e){
        logger.error("Could not sleep", e);
      }
      isRunning = false;
      //logger.debug("Loop stopped.");
    }
  }

}

