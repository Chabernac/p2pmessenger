package chabernac.p2pclient.gui;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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

import chabernac.gui.ApplicationLauncher;
import chabernac.protocol.facade.P2PFacadeException;
import chabernac.protocol.message.MessageIndicator;
import chabernac.protocol.message.MultiPeerMessage;
import chabernac.tools.Tools;


public class NewMessageDialog5 extends JDialog implements iMessageDialog{
  private static final long serialVersionUID = 4026463345239592657L;

  private static Logger logger = Logger.getLogger(NewMessageDialog5.class);
  private static NewMessageDialog5 INSTANCE = null;

  private JTextArea myText = null;
  private JTextArea myReplyText = null;
  private TitledBorder myBorder = null;
  private TitledBorder myReplyBorder = null;
  private ChatMediator myMediator = null;
  private MultiPeerMessage myMessage = null;
  private MultiPeerMessage myReplyMessage = null;
  private JPanel myContentPanel = null;
  private JPanel myWestPanel =  null;
  private InputMap myInputMap1 = null;
  private InputMap myInputMap2 = null;
  private JScrollPane myReplyScrollPane = null;
  private Robot myRobot;
  private boolean isEnveloppeAlwaysClosed = false;

  private ExecutorService myService = Executors.newFixedThreadPool( 1 );
  private ExecutorService mySendService = Executors.newFixedThreadPool( 5 );
//  private AtomicLong myMessageCounter = new AtomicLong(0);

  private Object VISIBLE_LOCK = new Object();

  private boolean isFirstTime = true;

  private List< Future > myPendingMessages = Collections.synchronizedList( new ArrayList< Future >());

  private NewMessageDialog5(ChatMediator aMediator){
    myMediator = aMediator;
    init();
    addListener();
    Tools.invokeLaterIfNotEventDispatchingThread(new Runnable(){
      public void run(){
        buildGUI();
        initActionMap();
        setAlwaysOnTop( true );
      }
    });
  }

  private void addListener(){
    addComponentListener(  new MyComponentAdapter() );
  }

  private void init(){
    myText = new JTextArea();
    myText.setEditable(false);
    myText.setLineWrap(true);
    myText.setWrapStyleWord(true);
    myText.setRows(4);
    myText.setColumns(20);
    myText.setToolTipText("r = reply, a = reply all, c = chat, x = discard all, o=open enveloppe");
    myReplyText = new JTextArea();
    myReplyText.setLineWrap(true);
    myReplyText.setWrapStyleWord(true);
    myReplyText.setRows(4);
    myReplyText.setColumns(20);
    myReplyBorder = new TitledBorder("");
    myReplyScrollPane = new JScrollPane(myReplyText, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    myReplyScrollPane.setBorder(myReplyBorder);
    myBorder = new TitledBorder("");
    try {
      myRobot = new Robot();
    } catch ( AWTException e ) {
      logger.error("Could not initialize robot", e);
    }
  }


  private void initActionMap(){
    myInputMap1 = new ComponentInputMap(myContentPanel);
    //InputMap theMap = myContentPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    myInputMap1.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "reply");
    myInputMap1.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), "replyall");
    myInputMap1.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, 0), "chat");
    myInputMap1.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, 0), "clear");
    myInputMap1.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, 0), "open");
    myInputMap1.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "sendorclose");
    myInputMap1.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "sendorclose");
    myInputMap1.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAUSE, 0), "nodialog");

    myInputMap2 = new ComponentInputMap(myContentPanel);
    myInputMap2.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "normalmode");
    myInputMap2.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "setinvisible");

    myContentPanel.setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, myInputMap1);

    ActionMap theActionMap = myContentPanel.getActionMap();
    theActionMap.put("reply", new ReplyAction());
    theActionMap.put("replyall", new ReplyAllAction());
    theActionMap.put("chat", new ChatAction());
    theActionMap.put("sendorclose", new SendOrCloseAction());
    theActionMap.put("normalmode", new NormalModeAction());
    theActionMap.put("nodialog", new NoDialogAction());
    theActionMap.put("setinvisible", new SetInvisibleAction());
    theActionMap.put("clear", new ClearAction());
    theActionMap.put("open", new OpenAction());



    InputMap theTextMap = myReplyText.getInputMap(JComponent.WHEN_FOCUSED);
    theTextMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "setinvisible");
    ActionMap theTextActionMap = myReplyText.getActionMap();
    theTextActionMap.put("setinvisible", new SetInvisibleAction());
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
    if(myMessage != null){
      SimpleDateFormat theFormat = new SimpleDateFormat("HH:mm");
      String theTitle = "Nieuw bericht " + theFormat.format(myMessage.getCreationTime());
      if(myPendingMessages.size() > 1){
        theTitle += " (+" + (myPendingMessages.size() - 1) + ")";
      }
      logger.debug("Setting title to: " + theTitle);
      System.out.println("Setting title to: " + theTitle);
      setTitle(theTitle) ;
      repaint();
    }
  }
  
  public boolean isEnveloppeAlwaysClosed() {
    return isEnveloppeAlwaysClosed;
  }

  public void setEnveloppeAlwaysClosed( boolean anEnveloppeAlwaysClosed ) {
    isEnveloppeAlwaysClosed = anEnveloppeAlwaysClosed;
  }

  private void setMessage(final MultiPeerMessage aMessage){
    System.out.println("Event dispatching thread: " + EventQueue.isDispatchThread());

    try{
      Tools.invokeLaterAndWaitIfNotEventDispatchingThread(   new Runnable(){
        public void run(){
          try{
            myMessage = aMessage;
            setTitle();

            String theUserList = Tools.getEnvelop(myMediator.getP2PFacade(), aMessage); 

            myBorder.setTitle(theUserList);
            //myText.setToolTipText(theUserList);
            if(isEnveloppeAlwaysClosed || aMessage.containsIndicator( MessageIndicator.CLOSED_ENVELOPPE )){
              myText.setText( "Typ 'o' om te openen'" );
            } else {
              myText.setText(aMessage.getMessage());
            }
            myText.setEditable(false);
            myReplyText.setText("");
            myReplyMessage = null;
            addRemoveReply(false);

            pack();
            repaint();
            setVisible( true );
            KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
            requestFocus(false);
            myText.requestFocus();
            requestFocusInWindow(false);
            if(myRobot != null && isFirstTime){
              Point thePoint = getLocationOnScreen();
              myRobot.mouseMove( thePoint.x + getWidth() - 50, thePoint.y + 30 );
              myRobot.mousePress( InputEvent.BUTTON1_MASK);
              myRobot.mouseRelease( InputEvent.BUTTON1_MASK);
//              isFirstTime = false;
            }
          }catch(P2PFacadeException e){
            logger.error( "Error occured while setting message", e );
          }
        }

      });
    }catch(Exception e){
      e.printStackTrace();
    }

    synchronized(VISIBLE_LOCK){
      while(isVisible()){
        try {
          VISIBLE_LOCK.wait();
        } catch ( InterruptedException e ) {
        }
      }
    }

    myText.requestFocus();
    sendOrClose();
  }


  public static synchronized NewMessageDialog5 getInstance(ChatMediator aMediator){
    if(INSTANCE == null) INSTANCE = new NewMessageDialog5(aMediator);
    return INSTANCE;
  }

  public void showMessage(final MultiPeerMessage aMessage){
//    myMessageCounter.incrementAndGet();
    try{
      final BlockingQueue<Future> theBlockingQueue = new ArrayBlockingQueue< Future >(1);
      Runnable theRunnable = new Runnable(){
        public void run(){
          setMessage( aMessage );
//        myMessageCounter.decrementAndGet();
          try{
            myPendingMessages.remove( theBlockingQueue.poll(5, TimeUnit.SECONDS) );
          }catch(InterruptedException e){
            logger.error("Interrupted", e);
          }
        }
      };
      Future theFuture = myService.submit(theRunnable);
      myPendingMessages.add(theFuture);
      theBlockingQueue.put(theFuture);
    }catch(InterruptedException e){
      logger.error("Interrupted", e);
    }

    Tools.invokeLaterIfNotEventDispatchingThread(new Runnable(){
      public void run(){
        setTitle();
      }
    });
  }

  private void removeFinishedTasks(){
    for(Iterator< Future > i = myPendingMessages.iterator();i.hasNext();){
      Future theFuture = i.next();
      if(theFuture.isDone()){
        i.remove();
      }
    }
  }

  public void cancelPendingTasks(){
    for(Iterator< Future > i = myPendingMessages.iterator();i.hasNext();){
      Future theFuture = i.next();
      theFuture.cancel( true );
      i.remove();
    }
  }

  private void clear(){
    setVisible(false);
    //TODO implement clear
  }

  private void sendOrClose(){
    logger.debug("Sending");

    if(myReplyMessage != null && !myReplyText.getText().equals("")){
      myReplyMessage = myReplyMessage.setMessage(myReplyText.getText());
      logger.debug("Sending message from dialog");
      myMediator.getP2PFacade().sendEncryptedMessage( myReplyMessage, mySendService);
      myMediator.setLastSendMessage(myReplyMessage);
    }
    setVisible(false);
  }

  private void addRemoveReply(final boolean reply){
    SwingUtilities.invokeLater(new Runnable(){
      public void run(){
        try{
          if(reply){
            myWestPanel.add(myReplyScrollPane, BorderLayout.WEST);
            myContentPanel.setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, myInputMap2);
            myReplyBorder.setTitle(Tools.getEnvelop(myMediator.getP2PFacade(), myReplyMessage));
            pack();
            myReplyText.requestFocus();
          } else {
            myWestPanel.remove(myReplyScrollPane);
            myContentPanel.setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, myInputMap1);
            //myText.setText(myMessage.getMessage());
            //myText.setEditable(false);
            myBorder.setTitle(Tools.getEnvelop(myMediator.getP2PFacade(), myMessage));
            pack();
            myText.requestFocus();
            myReplyMessage = null;
          }
        }catch(P2PFacadeException e){
          logger.error("An error occured when removing reply", e);
        }
      }
    });
  }



  private class ReplyAction extends AbstractAction{
    public void actionPerformed(ActionEvent e) {
      try{
        myReplyMessage = myMessage.reply();
        myReplyMessage = myReplyMessage.setSource( myMediator.getP2PFacade().getPeerId() );
        myMediator.getUserSelectionProvider().setSelectedUsers( myReplyMessage.getDestinations() );
        addRemoveReply(true);
      }catch(P2PFacadeException f){
        logger.error("Error occured while constructing reply message", f);
      }
    }
  }

  private class ReplyAllAction extends AbstractAction{
    public void actionPerformed(ActionEvent e) {
      try{
        myReplyMessage = myMessage.replyAll();
        myReplyMessage = myReplyMessage.setSource( myMediator.getP2PFacade().getPeerId() );
        myReplyMessage = myReplyMessage.removeDestination( myMediator.getP2PFacade().getPeerId() );
        myMediator.getUserSelectionProvider().setSelectedUsers( myReplyMessage.getDestinations() );
        addRemoveReply(true);
      }catch(P2PFacadeException f){
        logger.error("Error occured while constructing reply message", f);
      }
    }
  }

  private class SendOrCloseAction extends AbstractAction{
    public void actionPerformed(ActionEvent e) {
      sendOrClose();
    }
  }

  private class SetInvisibleAction extends AbstractAction{
    public void actionPerformed(ActionEvent e) {
      setVisible(false);
    }
  }

  private class ChatAction extends AbstractAction{
    public void actionPerformed(ActionEvent e) {
      MultiPeerMessage theReplyMessage = myMessage.replyAll();
      myMediator.getUserSelectionProvider().setSelectedUsers( theReplyMessage.getDestinations() );
      myMediator.getMessageProvider().setMessage( myReplyText.getText() );
      try {
        ApplicationLauncher.showChatFrame();
      } catch ( P2PFacadeException e1 ) {
        logger.error("Could not show gui", e1);
      }
    }
  }

  private class ClearAction extends ChatAction{
    public void actionPerformed(ActionEvent e) {
      //TODO implement clear
      super.actionPerformed(e);
    }
  }
  
  private class OpenAction extends ChatAction{
    public void actionPerformed(ActionEvent e) {
      myText.setText( myMessage.getMessage() );
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
      cancelPendingTasks();
      setVisible( false );
    }
  }

  private class MyComponentAdapter extends ComponentAdapter {
    public void componentHidden(ComponentEvent e) {
      synchronized ( VISIBLE_LOCK ) {
        VISIBLE_LOCK.notifyAll();
      }
    }
  }
}

