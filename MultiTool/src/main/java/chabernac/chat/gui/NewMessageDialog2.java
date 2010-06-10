package chabernac.chat.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import chabernac.chat.Message;
import chabernac.chat.gui.event.SelectUsersEvent;
import chabernac.chat.gui.event.ShowChatPanelEvent;
import chabernac.event.ApplicationEventDispatcher;
import chabernac.messengerservice.MessengerClientService;
import chabernac.queue.ArrayQueue;
import chabernac.queue.TriggeredQueueReader;
import chabernac.queue.TriggeringQueue;
import chabernac.queue.iObjectProcessor;
import chabernac.util.Tools;


public class NewMessageDialog2 extends TrueDialog implements iObjectProcessor{
  private static Logger logger = Logger.getLogger(NewMessageDialog2.class);

  private JTextArea myText = null;
  private JTextArea myReplyText = null;
  private TitledBorder myBorder = null;
  private TitledBorder myReplyBorder = null;
  private MessengerClientService myChatModel = null;
  private ChatMediator myMediator = null;
  private Message myMessage = null;
  private Message myReplyMessage = null;
  private JPanel myContentPanel = null;
  private JPanel myWestPanel =  null;
  private InputMap myInputMap1 = null;
  private InputMap myInputMap2 = null;
  private JScrollPane myReplyScrollPane = null;
  private boolean closing = false;
  private TriggeringQueue myQueue = null;
  private boolean inProgress = false;

  private static NewMessageDialog2 instance = null;

  private NewMessageDialog2(ChatMediator aMediator){
    myMediator = aMediator;
    myChatModel = myMediator.getMessengerClientService();
    init();
    Tools.invokeLaterIfNotEventDispatchingThread(new Runnable(){
      public void run(){
        buildGUI();
        initActionMap();
        addWindowListener();  
      }
    });
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
    myQueue = new TriggeringQueue(new ArrayQueue(50));
    TriggeredQueueReader theTriggeredQueueReader = new TriggeredQueueReader(myQueue, this);
    theTriggeredQueueReader.setThreads(1);
    setKeepInFront(true);
  }

  private void addWindowListener(){
    WindowAdapter theAdapter= new WindowAdapter(){
      
      public void windowActivated(WindowEvent e) {
        synchronized(NewMessageDialog2.this){
          logger.debug("Window activated");
          NewMessageDialog2.this.notifyAll();
        }
      }
      
      public void windowStateChanged(WindowEvent e) {
        synchronized(NewMessageDialog2.this){
          logger.debug("Window state changed: " + e);
          NewMessageDialog2.this.notifyAll();
        }
        
      }
      public void windowDeactivated(WindowEvent e) {
        synchronized(NewMessageDialog2.this){
          logger.debug("Window deactivated");
          NewMessageDialog2.this.notifyAll();
        }
      }
      public void windowClosing(WindowEvent evt){
        logger.debug("Window closing");
        sendOrClose();
      }
      public void windowLostFocus(WindowEvent e) {
        logger.debug("Window lost focus");
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
  
  private void setTitle(){
    SimpleDateFormat theFormat = new SimpleDateFormat("HH:mm");
    String theTitle = "Nieuw bericht " + theFormat.format(new Date(myMessage.getSendTime()));
    if(myQueue.size() > 0){
      theTitle += " (+" + myQueue.size() + ")";
    }
    setTitle(theTitle) ;
  }

  private synchronized void setMessage(final Message aMessage){
    System.out.println("Event dispatching thread: " + EventQueue.isDispatchThread());
    
    try{
    Tools.invokeLaterIfNotEventDispatchingThread(new Runnable(){
      public void run(){
        myMessage = aMessage;
        setTitle();
        
        String theUserList = Tools.getEnvelop(myMediator.getUserList(), aMessage); 

        myBorder.setTitle(theUserList);
        myText.setToolTipText(theUserList);
        myText.setText(aMessage.getMessage());
        myText.setEditable(false);
        myText.requestFocus();
        myContentPanel.setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, myInputMap1);
//      myContentPanel.requestFocus();
        pack();
        repaint();
      }

    });
    }catch(Exception e){
    	e.printStackTrace();
    }
    
    
    setVisible(true);
    System.out.println("1");
    forceToFront();
    System.out.println("2");
    waitForBack();
    System.out.println("3");
    setVisible(false);
    
    //setVisible(true);
//
    //logger.debug("Wait for activate: " + isActive());
//    
    //waitForActivate();
    
    
//    if(!isActive()){
//      Tools.requestFocus(NewMessageDialog2.this);
//      toFront();
//      requestFocus();
//      //myFocusWindowThread.startThread();
//    }
    //while(!isActive()){
//      logger.debug("Not active!");
//      Tools.invokeLaterIfNotEventDispatchingThread(new Thread(
//        new Runnable(){
//          public void run(){
//            Tools.requestFocus(NewMessageDialog2.this);
//            toFront();
//            requestFocus();
//          }
//        }
//      ));
//      
//      try{
//        Thread.yield();
//        if(!isActive())Thread.sleep(2000);
//        System.out.println(isActive());
//      }catch(Exception e){
//        e.printStackTrace();
//      }
    //}
    
    
//    logger.debug("Active: " + isActive());
//    
//    waitForDeactivate();

    //logger.debug("Starting window focus thread");
    //myFocusWindowThread.startThread();
    //if the popup is not active it is not visible, so we don't wait
    //if(isActive()){
//      while(isVisible()){
//        try {
//          logger.debug("Popup still visible, waiting....");
//          wait();
//        } catch (InterruptedException e) {
//          e.printStackTrace();
//        }
//      }
    //waitForDeactivate();  
//    } else {
//      logger.debug("Skipping message");
//      setVisible(false);
//    }
    //logger.debug("Stopping window focus thread");
    //myFocusWindowThread.stopThread();
  }
  
  private synchronized void waitForActivate(){
    logger.debug("active: " + isActive() + " visible: " + isVisible());
    show();
    //setVisible(true);
    Tools.requestFocus(NewMessageDialog2.this);
    toFront();
    while(!isActive() || !isVisible()){
      try{
        show();
        //setVisible(true);
        Tools.requestFocus(NewMessageDialog2.this);
        toFront();
        //requestFocus();
        logger.debug("Waiting for activate");
        wait(2000);
      }catch(InterruptedException e){
        e.printStackTrace();
      }
    }
  }
  
  private synchronized void waitForDeactivate(){
    while(isVisible()){
      try{
        logger.debug("Waiting for deactivate");
        wait();
      }catch(InterruptedException e){
        e.printStackTrace();
      }
    }
  }

  public static void creatIntance(ChatMediator aMediator){
    if(instance == null) instance = new NewMessageDialog2(aMediator);
  }

  public void showMessage(Message aMessage){
    if(inProgress){
      Tools.invokeLaterIfNotEventDispatchingThread(new Runnable(){
        public void run(){
          setTitle();
        }
      });
    }
    myQueue.put(aMessage);
  }

  public void processObject(Object anObject) {
    if(!(anObject instanceof Message)) return ;

    closing = false;

    synchronized (myQueue) {
      inProgress = true;

      Message theMessage = (Message)anObject;

      setMessage(theMessage);
      
      Thread.yield();

      synchronized(theMessage){
        theMessage.setRead(true);
        theMessage.notifyAll();
      }
      
      inProgress = false;
    }
  }

  public static void showDialog(Message aMessage){
    if(instance == null) return ;
    instance.showMessage(aMessage);
  }

  private void closeDialog(){
    Tools.invokeLaterIfNotEventDispatchingThread(new Runnable(){
      public void run(){
        if(!closing){
          closing = true;
          myReplyText.setText("");
          myReplyMessage = null;
          addRemoveReply(false);
          setVisible(false);

          synchronized(NewMessageDialog2.this){
            NewMessageDialog2.this.notifyAll();
          }
        }
      }
    });

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
    closeDialog();
  }

  private void addRemoveReply(final boolean reply){
    Tools.invokeLaterIfNotEventDispatchingThread(new Runnable(){
      public void run(){
        if(reply){
          myWestPanel.add(myReplyScrollPane, BorderLayout.WEST);
          //myText.setText(myReplyMessage.getMessage());
          //myText.setEditable(true);
          //myText.requestFocus();
          //myBorder.setTitle(myReplyMessage.getEnvelop());
          myContentPanel.setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, myInputMap2);
          myReplyBorder.setTitle(Tools.getEnvelop(myMediator.getUserList(), myReplyMessage));

//        AWTEvent theEvent = mySystemEventQueue.peekEvent();
//        if(theEvent instanceof KeyEvent){
//        KeyEvent theKeyEvent = (KeyEvent)theEvent;
//        if(theKeyEvent.getKeyChar() == 'r' || theKeyEvent.getKeyChar() == 'a'){
//        try{
//        theEvent = mySystemEventQueue.getNextEvent();
//        }catch(InterruptedException e){
//        Logger.log(this,"Error while getting event from event queue", e);
//        }
//        }
//        }
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
    });
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
      try{
        Message theReplyMessage = myMessage.replyAll(myChatModel.getUser().getUserName());
        ApplicationEventDispatcher.fireEvent(new SelectUsersEvent(theReplyMessage.getTo()));
        ApplicationEventDispatcher.fireEvent(new ShowChatPanelEvent());
        closeDialog();
      }catch(RemoteException f){
        logger.error("An error occured in ChatAction", f);
      }
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
          logger.debug("Trying to get the popup in front");
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

