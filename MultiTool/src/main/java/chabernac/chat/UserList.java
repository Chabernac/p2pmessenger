package chabernac.chat;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import chabernac.application.SavedApplicationModel;
import chabernac.chat.gui.event.TotalUserListChangedEvent;
import chabernac.event.ApplicationEventDispatcher;
import chabernac.event.Event;
import chabernac.event.iEventListener;
import chabernac.io.IOOperator;
import chabernac.messengerservice.MessengerClientService;
import chabernac.messengerservice.MessengerUser;
import chabernac.messengerservice.event.UserChangedEvent;
import chabernac.messengerservice.event.UserListChangedEvent;
import chabernac.messengerservice.event.UserRemovedEvent;

public class UserList extends SavedApplicationModel{
  private static Logger LOGGER = Logger.getLogger(UserList.class);
  
  public static final String USERS_CHANGED = "USERS_CHANGED";

  private HashMap myUsers = null;
  private HashMap myGroups = null;
  private static final File USER_FILE = new File("users.bin");
  private static final File GROUP_FILE = new File("groups.bin");
  private MessengerClientService myMessengerClientService = null;
  //private DefaultObservable myObservable = null;

  public UserList(MessengerClientService aMessengerClientService){
    super();
    myMessengerClientService = aMessengerClientService;
    
    //myMessengerClientService.addObserver(this);
    addListeners();
    init();
    load();
  }
  
  private void addListeners(){
    ApplicationEventDispatcher.addListener(new UserListChangedEventListener(), new Class[]{UserListChangedEvent.class, UserChangedEvent.class, UserRemovedEvent.class});
  }

  private void init(){
    myUsers = new HashMap();
    myGroups = new HashMap();
    //myObservable = new DefaultObservable(this);
  }

//  public void addObserver(Observer anObserver){
//    myObservable.addObserver(anObserver);
//  }

//  public void deleteObserver(Observer anObserver){
//    myObservable.deleteObserver(anObserver);
//  }

  public void load(){
    Object theUsers = IOOperator.loadObject(USER_FILE);
    if(theUsers != null) {
      myUsers = (HashMap)theUsers;
      for(Iterator i=myUsers.values().iterator();i.hasNext();){
        //reset all the status to offline
        ((MessengerUser)i.next()).setStatus(MessengerUser.OFFLINE);
      }
    }
    Object theGroups = IOOperator.loadObject(GROUP_FILE);
    if(theGroups != null) myGroups = (HashMap)theGroups;
  }

  public void save(){
    IOOperator.saveObject(myUsers, USER_FILE);
    IOOperator.saveObject(myGroups, GROUP_FILE);
  }

  private class UserListChangedEventListener implements iEventListener{

    public void eventFired(Event anEvt) {
      for(Iterator i=myMessengerClientService.getUsers().values().iterator();i.hasNext();){
        MessengerUser theUser = (MessengerUser)i.next();
        myUsers.put(theUser.getId(), theUser);
        //LOGGER.debug("Online: " + theUser.getUserName());
      }

      for(Iterator i=myUsers.values().iterator();i.hasNext();){
        MessengerUser theUser = (MessengerUser)i.next();
        if(!myMessengerClientService.getUsers().containsKey(theUser.getId())){
          theUser.setStatus(MessengerUser.OFFLINE);
          //LOGGER.debug("Offline: " + theUser.getUserName());
        }
      }
      
      //LOGGER.debug("Firing new TotalUserListChangedEvent");
      ApplicationEventDispatcher.fireEvent(new TotalUserListChangedEvent(myUsers));
      //myObservable.notifyObs(USERS_CHANGED);

    }
    
  }

  public HashMap getUsers(){
    return myUsers;
  }

  public void removeUser(String aUserName){
    myUsers.remove(aUserName);
  }

  public HashMap getGroups(){
    return myGroups;
  }

  public void addGroupMember(String aGroup, MessengerUser aMember){
    if(!myGroups.containsKey(aGroup)){
      HashMap theGroupMembers = new HashMap();
      myGroups.put(aGroup, theGroupMembers);
    }

    HashMap theGroupMembers = (HashMap)myGroups.get(aGroup);
    theGroupMembers.put(aMember.getUserName(), aMember);
  }

  public void removeGroup(String aGroup){
    myGroups.remove(aGroup);
  }

  public HashMap getGroupMembers(String aGroup){
    if(!myGroups.containsKey(aGroup)) return new HashMap();
    return (HashMap)myGroups.get(aGroup);
  }

  public MessengerUser getUser(String aUser){
    return (MessengerUser)myUsers.get(aUser);
  }

  public String getShortName(String aUser){
    if(!getUsers().containsKey(aUser)) return aUser;
    return getUser(aUser).getShortName();
  }
  
  public void removeOfflineUsers(){
    for(Iterator i=myUsers.values().iterator();i.hasNext();){
      //reset all the status to offline
      MessengerUser theUser = ((MessengerUser)i.next());
      if(theUser.getStatus() == MessengerUser.OFFLINE){
        i.remove();
      }
    }
    ApplicationEventDispatcher.fireEvent(new TotalUserListChangedEvent(myUsers));
  }

}

