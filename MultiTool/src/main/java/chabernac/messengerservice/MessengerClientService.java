package chabernac.messengerservice;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import chabernac.chat.Message;
import chabernac.chat.gui.event.ChangeUserStatusEvent;
import chabernac.chat.gui.event.UserAlreadyLoggedOnEvent;
import chabernac.command.Command;
import chabernac.event.ApplicationEventDispatcher;
import chabernac.event.Event;
import chabernac.event.iEventListener;
import chabernac.messengerservice.event.LoggedOffEvent;
import chabernac.messengerservice.event.LoggedOnEvent;
import chabernac.messengerservice.event.MessageDeliveredEvent;
import chabernac.messengerservice.event.MessageReceivedEvent;
import chabernac.messengerservice.event.MessageSendEvent;
import chabernac.messengerservice.event.RefreshUserEvent;
import chabernac.messengerservice.event.UserChangedEvent;
import chabernac.messengerservice.event.UserInfoSendEvent;
import chabernac.messengerservice.event.UserListChangedEvent;
import chabernac.messengerservice.event.UserRemovedEvent;
import chabernac.thread.ThreadPool;
import chabernac.thread.iThreadPoolListener;
import chabernac.timer.CommandTimer;

public class MessengerClientService extends UnicastRemoteObject implements iMessengerClientService, Command{
	private static final Logger logger = Logger.getLogger(MessengerClientService.class);
//	public static final String USER_CHANGED = "USER CHANGED";
//	public static final String MESSAGE_RECEIVED = "MESSAGE_RECEIVED";
//	public static final String MESSAGE_SEND = "MESSAGE_SEND";


	private MessengerUser myUser = null;
	private Map myUsers = null;
	//private DefaultObservable myObservable = null;
	private String myServer = null;
	private int myServerPort;
	private String myServiceURL = "";
	private String myServerServiceURL = "";
	private boolean sendLocal = true;
	private boolean isHideTo = false;
	private Message myLastMessageReceived = null;
	private Message myLastMessageSend = null;
	private CommandTimer myTimer = null;
	private boolean isRegistered = false;

	public MessengerClientService(MessengerUser aUser, String aServer, int aPort) throws RemoteException, MalformedURLException, NotBoundException {
		super();
		myUser = aUser;
		myServer = aServer; 
		myServerPort = aPort;
		init();
		addListeners();
	}

	private void init(){
		myUsers = Collections.synchronizedMap(new HashMap());
		//myObservable = new DefaultObservable(this);
		myServiceURL = "rmi://"  + myUser.getHost() + ":" + myUser.getRmiPort() + "/MessengerClientService";
		myServerServiceURL = "rmi://"  + myServer + ":" + myServerPort + "/MessengerService";
		myTimer = new CommandTimer(5 * 60 * 1000);
//		myTimer = new CommandTimer(30 * 1000);
	}

	private void addListeners(){
		ApplicationEventDispatcher.addListener(new EventListener(), new Class[]{ChangeUserStatusEvent.class, RefreshUserEvent.class});
	}

	public void register() {
		myTimer.addCommand(this);
		myTimer.startTimer();
	}

	private void doRegister() throws MalformedURLException, RemoteException, NotBoundException, UserInUseException{
		Naming.rebind(myServiceURL, this);
		iMessengerService theMessengerService = (iMessengerService)Naming.lookup(myServerServiceURL);
		theMessengerService.registerUser(myUser);
		ApplicationEventDispatcher.fireEvent(new LoggedOnEvent(myUser));
		myUsers = theMessengerService.getAllUsers();
		ApplicationEventDispatcher.fireEvent(new UserListChangedEvent(myUsers));
		isRegistered = true;
	}

	public void refreshUsers() throws MalformedURLException, RemoteException, NotBoundException{
		logger.debug("Refreshing user list");
		iMessengerService theMessengerService = (iMessengerService)Naming.lookup(myServerServiceURL);
		myUsers = theMessengerService.getAllUsers();
		for(Iterator i=myUsers.values().iterator();i.hasNext();){
			((MessengerUser)i.next()).setLastActiveDateTime(new Date());
		}
		ApplicationEventDispatcher.fireEvent(new UserListChangedEvent(myUsers));
		//myObservable.notifyObs(USER_CHANGED);
	}

	public void sendUserInfo() throws MalformedURLException, RemoteException, NotBoundException{
		logger.debug("Sending user info to server");
		iMessengerService theMessengerService = (iMessengerService)Naming.lookup(myServerServiceURL);
		theMessengerService.updateUser(myUser);
		ApplicationEventDispatcher.fireEvent(new UserInfoSendEvent(myUser));
	}

	public void unregister() throws RemoteException, MalformedURLException, NotBoundException{
		iMessengerService theMessengerService = (iMessengerService)Naming.lookup(myServerServiceURL);
		logger.debug("Uregistering");
		theMessengerService.unregisterUser(myUser);
		logger.debug("Unbinding");
		Naming.unbind(myServiceURL);
		myTimer.stop();
		ApplicationEventDispatcher.fireEvent(new LoggedOffEvent(myUser));
	}

	private iMessengerClientService getClientService(MessengerUser aUser) throws MalformedURLException, RemoteException, NotBoundException{
		String theServiceURL = "rmi://"  + aUser.getHost() + ":" + aUser.getRmiPort() + "/MessengerClientService";
		return (iMessengerClientService)Naming.lookup(theServiceURL);
	}

//	public void addObserver(Observer anObserver){
//	myObservable.addObserver(anObserver);
//	}

//	public void removeObserver(Observer anObserver){
//	myObservable.deleteObserver(anObserver);
//	}

	private static final long serialVersionUID = 4965833912899808777L;

	public MessengerUser getUser() throws RemoteException {
		return myUser;
	}

	public Map getUsers(){
		//always return an unmodifiable copy
		return Collections.unmodifiableMap(new HashMap(myUsers));
	}

	public MessengerUser getUser(String aUserName){
		if(!myUsers.containsKey(aUserName)) return null;
		return (MessengerUser)myUsers.get(aUserName);
	}

	public void userChanged(MessengerUser aUser) throws RemoteException {
		logger.debug("User update received: " + aUser.getUserName());
		aUser.setLastActiveDateTime(new Date());
		myUsers.put(aUser.getUserName(), aUser);
		//myObservable.notifyObs(USER_CHANGED);
		ApplicationEventDispatcher.fireEvent(new UserChangedEvent(aUser));
	}

	public void removeUser(MessengerUser aUser) throws RemoteException {
		myUsers.remove(aUser.getUserName());
		//myObservable.notifyObs(USER_CHANGED);
		ApplicationEventDispatcher.fireEvent(new UserRemovedEvent(aUser));
	}

	public void acceptMessage(Message aMessage) throws RemoteException {
		logger.debug("New message received");
		myLastMessageReceived = aMessage;
		//myObservable.notifyObs(MESSAGE_RECEIVED);
		ApplicationEventDispatcher.fireEvent(new MessageReceivedEvent(aMessage));
		if(!myUsers.containsKey( aMessage.getFrom() )){
		  ApplicationEventDispatcher.fireEvent( new RefreshUserEvent() );
		}
	}

	public void sendMessage(final Message aMessage) throws RemoteException {
		aMessage.setSendTime(System.currentTimeMillis());
		aMessage.setHiddenTo(isHideTo());
		final ArrayList to = new ArrayList();
		to.addAll(aMessage.getTo());
		if(isSendLocal() && !to.contains(myUser.getId())) to.add(myUser.getId());

		myLastMessageSend = aMessage;

		aMessage.prepareAttachments();
		//final MessageReducer theReducer = new MessageReducer(aMessage, to.size());

		ThreadPool thePool = new ThreadPool();

		for(Iterator i = to.iterator(); i.hasNext();){
			final String who = (String)i.next();
			if(myUsers.containsKey(who)){
				final MessengerUser theUser = (MessengerUser)myUsers.get(who);
				thePool.addRunnable(new Runnable(){
					public void run(){
						try {
							aMessage.setStatus(who, Message.SEND_IN_PROGRES);
							iMessengerClientService theClientService = getClientService(theUser);
							theClientService.acceptMessage(aMessage);
							aMessage.setStatus(who, Message.SEND);
						} catch (Exception e) {
							logger.error("An error occured while sending message to: " + theUser, e);
							aMessage.setStatus(who, Message.FAILED);
						} 
					}
				});
			} else {
				aMessage.setStatus(who, Message.FAILED);
			}
		}

		ApplicationEventDispatcher.fireEvent(new MessageSendEvent(aMessage));

		thePool.addListener(new iThreadPoolListener(){
			public void threadsFinished() {
				aMessage.cleanHeavyWeightAttachments();
				aMessage.setSend(true);
				ApplicationEventDispatcher.fireEvent(new MessageDeliveredEvent(aMessage));
				//myObservable.notifyObs(MESSAGE_SEND);
			}
		});

		thePool.startThreads();
	}




	public boolean isSendLocal() {
		return sendLocal;
	}

	public void setSendLocal(boolean sendLocal) {
		this.sendLocal = sendLocal;
	}

	public Message getLastMessageSend(){
		return myLastMessageSend;
	}

	public Message getLastMessageReceived(){
		return myLastMessageReceived;
	}

	public boolean isHideTo() {
		return isHideTo;
	}

	public void setHideTo(boolean isHideTo) {
		this.isHideTo = isHideTo;
	}

	private class MessageReducer{
		private Message myMessage = null;
		private int myCounter;

		public MessageReducer(Message aMessage, int counter){
			myMessage = aMessage;
			myCounter = counter;
		}

		public synchronized void reduce(){
			if(--myCounter <= 0){
				myMessage.cleanHeavyWeightAttachments();
			}
		}
	}

	public void execute() {
		try{
			if(!isRegistered) {
				doRegister();
			} else {
				sendUserInfo();
				refreshUsers();
			}
		}catch(UserInUseException e){
			ApplicationEventDispatcher.fireEvent(new UserAlreadyLoggedOnEvent(e.getUser()));
		}catch(Exception e){
			logger.error("An error occured while refreshing", e);
		}
	}

	private class EventListener implements iEventListener{
		public void eventFired(Event anEvt) {
			if(anEvt instanceof ChangeUserStatusEvent){
				ChangeUserStatusEvent theEvent = (ChangeUserStatusEvent)anEvt;
				myUser.setStatus(theEvent.getNewStatus());
				try {
					sendUserInfo();
				} catch (Exception e) {
					logger.error("Unable to send user info", e);
				}
			} else if(anEvt instanceof RefreshUserEvent){
			  try {
          refreshUsers();
        } catch ( Exception e ) {
          logger.error("Unable to refresh users", e);
        }
			}
		}
	}


}
