package chabernac.messengerservice;

import java.rmi.RemoteException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import chabernac.chat.Message;
import chabernac.event.ApplicationEventDispatcher;
import chabernac.event.Event;
import chabernac.event.iEventListener;
import chabernac.messengerservice.event.MessageReceivedEvent;
import chabernac.messengerservice.event.MessageSelectedEvent;
import chabernac.messengerservice.event.MessageSendEvent;
import chabernac.messengerservice.event.ReceivedMessagesUpdatedEvent;
import chabernac.messengerservice.event.SendMessagesUpdated;

public class MessageArchive {
  private static final Logger LOGGER = Logger.getLogger(MessageArchive.class);
  
  public static final String MESSAGE_RECEIVED = "MESSAGE_RECEIVED";
  public static final String MESSAGE_SEND = "MESSAGE_SEND";
  public static final String SEND_MESSAGES_CLEARED = "SEND_MESSAGES_CLEARED";
  public static final String RECEIVED_MESSAGES_CLEARED = "RECEIVED_MESSAGES_CLEARED";
  public static final String SELECTED_MESSAGE = "SELECTED_MESSAGE";
  
  private ArrayList mySendMessages = null;
  private ArrayList myReceivedMessages = null;
  private ArrayList myAllMessages = null;
  private MessengerClientService myService = null;
  
  private int currentMessage = 0;
  
  public MessageArchive(MessengerClientService aService){
    myService = aService;
    ApplicationEventDispatcher.addListener(new MessageSendListener(), MessageSendEvent.class);
    ApplicationEventDispatcher.addListener(new MessageReceivedListener(), MessageReceivedEvent.class);
    //myService.addObserver(this);
    init();
  }
  
  private void init(){
    mySendMessages = new ArrayList();
    myReceivedMessages = new ArrayList();
    myAllMessages = new ArrayList();
  }

  private class MessageSendListener implements iEventListener{

    public void eventFired(Event anEvt) {
      MessageSendEvent theEvent = (MessageSendEvent)anEvt;
      mySendMessages.add(theEvent.getMessage());
      myAllMessages.add(theEvent.getMessage());
      currentMessage = mySendMessages.size();
      ApplicationEventDispatcher.fireEvent(new SendMessagesUpdated(mySendMessages));
    }
  }
  
  private class MessageReceivedListener implements iEventListener{

    public void eventFired(Event anEvt) {
      MessageReceivedEvent theEvent = (MessageReceivedEvent)anEvt;
      LOGGER.debug("Adding message to received messages array");
      myReceivedMessages.add(theEvent.getMessage());
      myAllMessages.add(theEvent.getMessage());
      LOGGER.debug("firing ReceivedMessagesUpdatedEvent event");
      ApplicationEventDispatcher.fireEvent(new ReceivedMessagesUpdatedEvent(myReceivedMessages));
    }
  }
  /*
  public void update(Observable o, Object arg) {
    if(arg.equals(MessengerClientService.MESSAGE_RECEIVED)){
      //only add non technical messages
      if(!myService.getLastMessageReceived().isTechnicalMessage()){
        myReceivedMessages.add(myService.getLastMessageReceived());
        myAllMessages.add(myService.getLastMessageReceived());
        notifyObs(MESSAGE_RECEIVED);
      }
    } else  if(arg.equals(MessengerClientService.MESSAGE_SEND)){
      mySendMessages.add(myService.getLastMessageSend());
      myAllMessages.add(myService.getLastMessageSend());
      notifyObs(MESSAGE_SEND);
      setSelectedMessage(null);
    }
  }
  */
  
  /*
  public void notifyObs(String arg){
    setChanged();
    notifyObservers(arg);
  }
  */
  
  public ArrayList getSendMessages(){
    return mySendMessages;
  }
  
  public void clearSend(){
    mySendMessages.clear();
    ApplicationEventDispatcher.fireEvent(new SendMessagesUpdated(mySendMessages));
//    notifyObs(SEND_MESSAGES_CLEARED);
  }
  
  public void clearReceived(){
    myReceivedMessages.clear();
    ApplicationEventDispatcher.fireEvent(new ReceivedMessagesUpdatedEvent(myReceivedMessages));
//    notifyObs(RECEIVED_MESSAGES_CLEARED);
  }

  public ArrayList getReceivedMessages(){
    return myReceivedMessages;
  }
  
  public ArrayList getAllMessages(){
    return myAllMessages;
  }
  
  public Message getLastMessageSend(){
    if(mySendMessages.size() == 0) return null;
    return (Message)mySendMessages.get(mySendMessages.size() - 1);
  }
  
  public Message getLastMessageReceived(){
    if(myReceivedMessages.size() == 0) return null;
    return (Message)myReceivedMessages.get(myReceivedMessages.size() - 1);
  }
  
  public Message getLastMessageReceivedFromOther() throws RemoteException{
    for(int i=myReceivedMessages.size() -1;i>=0;i--){
      Message theMessage = (Message)myReceivedMessages.get(i);
      if(!theMessage.getFrom().equalsIgnoreCase(myService.getUser().getUserName())){
        return theMessage;
      }
    }
    return null;
  }
  
  public int getCurrentMessage(){
    return currentMessage;
  }
  
  public Message getSelectedMessage(){
    if(currentMessage >= mySendMessages.size()) return null;
    return (Message)mySendMessages.get(currentMessage);
  }
  
  public void setSelectedMessage(int which){
    if(which < 0) currentMessage = 0;
    else if(which > mySendMessages.size()) currentMessage = mySendMessages.size();
    else currentMessage = which;
    
    Message theMessage = null;
    
    if(currentMessage < mySendMessages.size()){
      theMessage = (Message)mySendMessages.get(currentMessage);
    } 
    
    ApplicationEventDispatcher.fireEvent(new MessageSelectedEvent(theMessage));
//    notifyObs(SELECTED_MESSAGE);
  }
  
  public void next(){
    setSelectedMessage(++currentMessage);
  }
  
  public void previous(){
    setSelectedMessage(--currentMessage);
  }
  
  public void removeMessage(Message aMessage) {
    int index = mySendMessages.indexOf(aMessage);
    mySendMessages.remove(aMessage);
    setSelectedMessage(index);
  }
  
  
  public void setSelectedMessage(Message aMessage){
    if(aMessage == null){
      currentMessage = mySendMessages.size();
    } else if(mySendMessages.contains(aMessage)){
      currentMessage = mySendMessages.indexOf(aMessage);
    }
    Message theMessage = null;
    if(currentMessage < mySendMessages.size()) theMessage = (Message)mySendMessages.get(currentMessage);
    ApplicationEventDispatcher.fireEvent(new MessageSelectedEvent(theMessage));
    //notifyObs(SELECTED_MESSAGE);
  }
  
  public void save(Message aMessage) {
    if(!mySendMessages.contains(aMessage)){
      mySendMessages.add(aMessage);
    }
  }

}
