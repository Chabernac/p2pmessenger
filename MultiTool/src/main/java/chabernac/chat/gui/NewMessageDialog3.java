package chabernac.chat.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ComponentInputMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
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


public class NewMessageDialog3 extends TrueDialog implements iObjectProcessor{
	private static Logger logger = Logger.getLogger(NewMessageDialog3.class);

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
//	private boolean closing = false;
	private TriggeringQueue myQueue = null;

	private static NewMessageDialog3 instance = null;

	private NewMessageDialog3(ChatMediator aMediator){
		myMediator = aMediator;
		myChatModel = myMediator.getMessengerClientService();
		init();
		Tools.invokeLaterIfNotEventDispatchingThread(new Runnable(){
			public void run(){
				buildGUI();
				initActionMap();
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
//		myText.setToolTipText("r = reply, a = reply all, c = chat");
		myReplyText = new JTextArea();
		myReplyText.setLineWrap(true);
		myReplyText.setWrapStyleWord(true);
		myReplyText.setRows(4);
		myReplyText.setColumns(20);
		myReplyBorder = new TitledBorder("");
		myReplyScrollPane = new JScrollPane(myReplyText, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		myReplyScrollPane.setBorder(myReplyBorder);
		myBorder = new TitledBorder("");
		myQueue = new TriggeringQueue(new ArrayQueue(300));
		TriggeredQueueReader theTriggeredQueueReader = new TriggeredQueueReader(myQueue, this);
		theTriggeredQueueReader.setThreads(1);
	}


	private void initActionMap(){
		myInputMap1 = new ComponentInputMap(myContentPanel);
		//InputMap theMap = myContentPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		myInputMap1.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "reply");
		myInputMap1.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), "replyall");
		myInputMap1.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, 0), "chat");
		myInputMap1.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, 0), "clear");
		myInputMap1.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "setinvisible");
		myInputMap1.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "setinvisible");
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
			String theTitle = "Nieuw bericht " + theFormat.format(new Date(myMessage.getSendTime()));
			if(myQueue.size() > 0){
				theTitle += " (+" + myQueue.size() + ")";
			}
			logger.debug("Setting title to: " + theTitle);
			setTitle(theTitle) ;
			repaint();
		}
	}

	private void setMessage(final Message aMessage){
		System.out.println("Event dispatching thread: " + EventQueue.isDispatchThread());

//		try{
		Tools.invokeLaterIfNotEventDispatchingThread(new Runnable(){
			public void run(){
				myMessage = aMessage;
				setTitle();

				String theUserList = Tools.getEnvelop(myMediator.getUserList(), aMessage); 

				myBorder.setTitle(theUserList);
				//myText.setToolTipText(theUserList);
				myText.setText(aMessage.getMessage());
				myText.setEditable(false);
				myReplyText.setText("");
				myReplyMessage = null;
				addRemoveReply(false);
				myText.requestFocus();
				//myContentPanel.setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, myInputMap1);
//				myContentPanel.requestFocus();
				pack();
				repaint();
			}

		});
//		}catch(Exception e){
//		e.printStackTrace();
//		}


		setDebugEnabled(false);
		forceToFront();
		myText.requestFocus();
		setDebugEnabled(false);
		waitForBack();
		setVisible(false);
		sendOrClose();
	}


	public static void creatIntance(ChatMediator aMediator){
		if(instance == null) instance = new NewMessageDialog3(aMediator);
	}

	public void showMessage(Message aMessage){
		myQueue.put(aMessage);
		Tools.invokeLaterIfNotEventDispatchingThread(new Runnable(){
			public void run(){
				setTitle();
			}
		});
	}

	public void processObject(Object anObject) {
		if(!(anObject instanceof Message)) return ;


		Message theMessage = (Message)anObject;

		setMessage(theMessage);

	}

	public static void showDialog(Message aMessage){
		if(instance == null) return ;
		instance.showMessage(aMessage);
	}

	public static void clearMessages(){
		if(instance == null) return ;
		instance.clear();
	}

	private void clear(){
		setVisible(false);
		while(myQueue.size() > 0){
			myQueue.get();
		}
	}

	private void sendOrClose(){
		logger.debug("Sending");

		if(myReplyMessage != null && !myReplyText.getText().equals("")){
			myReplyMessage.setMessage(myReplyText.getText());
			try {
				logger.debug("Sending message from dialog");
				myChatModel.sendMessage(myReplyMessage);
			} catch (RemoteException e) {
				logger.error("An error occured while sending message", e);
			}
		}
		setVisible(false);

		//closeDialog();
	}

	private void addRemoveReply(final boolean reply){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				if(reply){
					myWestPanel.add(myReplyScrollPane, BorderLayout.WEST);
					myContentPanel.setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, myInputMap2);
					myReplyBorder.setTitle(Tools.getEnvelop(myMediator.getUserList(), myReplyMessage));
					pack();
					myReplyText.requestFocus();
				} else {
					myWestPanel.remove(myReplyScrollPane);
					myContentPanel.setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, myInputMap1);
					//myText.setText(myMessage.getMessage());
					//myText.setEditable(false);
					myBorder.setTitle(Tools.getEnvelop(myMediator.getUserList(), myMessage));
					pack();
					myText.requestFocus();
					myReplyMessage = null;
				}
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

	private class SetInvisibleAction extends AbstractAction{
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
		}
	}

	private class ChatAction extends AbstractAction{
		public void actionPerformed(ActionEvent e) {
			try{
				Message theReplyMessage = myMessage.replyAll(myChatModel.getUser().getUserName());
				ApplicationEventDispatcher.fireEvent(new SelectUsersEvent(theReplyMessage.getTo()));
				ApplicationEventDispatcher.fireEvent(new ShowChatPanelEvent());
				setVisible(false);
				//closeDialog();
			}catch(RemoteException f){
				logger.error("An error occured in ChatAction", f);
			}
		}
	}
	
	private class ClearAction extends ChatAction{

		public void actionPerformed(ActionEvent e) {
      myQueue.clear();
			super.actionPerformed(e);
			
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
}

