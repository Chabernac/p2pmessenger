/*
 * Created on 17-feb-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.chat.gui;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import chabernac.chat.UserList;
import chabernac.chat.gui.event.ChangeUserStatusEvent;
import chabernac.chat.gui.event.SelectUsersEvent;
import chabernac.command.AbstractCommand;
import chabernac.event.ApplicationEventDispatcher;
import chabernac.event.Event;
import chabernac.event.iEventListener;
import chabernac.gui.CommandMenuItem;
import chabernac.gui.GPanelPopupMenu;
import chabernac.messengerservice.MessengerUser;

public class UserListPanelPopup extends GPanelPopupMenu {
  private static final Logger logger = Logger.getLogger(UserListPanelPopup.class);

  private UserListPanel myPanel = null;
  private ChatMediator myMediator = null;
  private String lastSelectedGroup = "";

  public UserListPanelPopup(UserListPanel aPanel, ChatMediator aMediator){
    super(aPanel);
    myPanel = aPanel;
    myMediator = aMediator;
    buildMenu();
  }

  private void buildMenu(){
    removeAll();
    add(new CommandMenuItem(new ClearCommand()));
    add(new CommandMenuItem(new NewGroupCommand()));
    add(new CommandMenuItem(new RemoveGroupCommand()));
    add(new CommandMenuItem(new RemoveOfflineUsers()));
    addSeparator();
    buildGroups();
    addSeparator();
    add(new CommandMenuItem(new HideCommand()));
    addSeparator();

    StatusCommand theAvailableCommand = new StatusCommand(MessengerUser.ONLINE); 
    StatusCommand theAwayCommand = new StatusCommand(MessengerUser.AWAY);
    StatusCommand theBussyCommand = new StatusCommand(MessengerUser.BUSSY);

    CommandMenuItem theAvailableItem = new CommandMenuItem(theAvailableCommand);
    CommandMenuItem theAwayItem = new CommandMenuItem(theAwayCommand);
    CommandMenuItem theBussyItem = new CommandMenuItem(theBussyCommand);

    theAvailableCommand.addObserver(theAwayItem);
    theAvailableCommand.addObserver(theBussyItem);

    theAwayCommand.addObserver(theAvailableItem);
    theAwayCommand.addObserver(theBussyItem);

    theBussyCommand.addObserver(theAvailableItem);
    theBussyCommand.addObserver(theAwayItem);

    add(theAvailableItem);
    add(theAwayItem);
    add(theBussyItem);
    
    addSeparator();
    
  }

  private void buildGroups(){
    for(Iterator i=myMediator.getUserList().getGroups().keySet().iterator();i.hasNext();){
      add(new CommandMenuItem(new SelectGroupCommand((String)i.next())));
    }
  }

  private class ClearCommand extends AbstractCommand{
    public String getName() { return "Clear"; }
    public boolean isEnabled() { return true; }
    public void execute() { myPanel.clearCheckBoxes(); }
  }

  private class NewGroupCommand extends AbstractCommand{
    public String getName() { return "New group"; }
    public boolean isEnabled() { return true; }
    public void execute() {
      String groupName = JOptionPane.showInputDialog(myPanel, "Group name");
      if(groupName == null || groupName.equals("")) return;
      UserList theUserList = myMediator.getUserList();
      theUserList.removeGroup(groupName);
      ArrayList theUsers = myPanel.getSelectedUsers();
      for(int i=0;i<theUsers.size();i++) {
        theUserList.addGroupMember(groupName, theUserList.getUser((String)theUsers.get(i)));
      }

      buildMenu();
    }
  }

  private class RemoveGroupCommand extends AbstractCommand{
    public String getName() { return "Remove group"; }
    public boolean isEnabled() { return true; }
    public void execute() {
      myMediator.getUserList().removeGroup(lastSelectedGroup);
      buildMenu();
    }
  }

  private class SelectGroupCommand extends AbstractCommand{
    private String myGroup = null;

    public SelectGroupCommand(String aGroup){
      myGroup = aGroup;
    }

    public String getName() { return myGroup; }

    public boolean isEnabled() { return true; }

    public void execute(){
      lastSelectedGroup = myGroup;
      myPanel.clearCheckBoxes();
      ApplicationEventDispatcher.fireEvent(new SelectUsersEvent(new ArrayList(myMediator.getUserList().getGroupMembers(myGroup).keySet())));
    }
  }

  private class HideCommand extends AbstractCommand{
    public String getName() {
      if(myMediator.getMessengerClientService().isHideTo()){
        return "Unhide to";
      } else {
        return "Hide to";
      }
    }

    public boolean isEnabled(){ return true;    }

    public void execute() {
      myMediator.getMessengerClientService().setHideTo(!myMediator.getMessengerClientService().isHideTo());
      notifyObs();
    }
  }

  private class StatusCommand extends AbstractCommand implements iEventListener{
    private int myStatus = MessengerUser.ONLINE;
    private int myCurrentStatus = -1;

    public StatusCommand(int aStatus){
      myStatus = aStatus;
      ApplicationEventDispatcher.addListener(this, ChangeUserStatusEvent.class);
    }

    public String getName() {
      switch(myStatus){
      case MessengerUser.ONLINE: return "Online";
      case MessengerUser.AWAY: return "Eventjes weg";
      case MessengerUser.BUSSY: return "Ben bezig";
      }
      return "";
    }

    public boolean isEnabled() {
    	return myStatus != myCurrentStatus;
    }

    public void execute() {
    	ApplicationEventDispatcher.fireEvent(new ChangeUserStatusEvent(myStatus));
    }

		public void eventFired(Event anEvt) {
			ChangeUserStatusEvent theEvent = (ChangeUserStatusEvent)anEvt;
			myCurrentStatus = theEvent.getNewStatus();
			notifyObs();
		}
  }
  
  private class RemoveOfflineUsers extends AbstractCommand{

    public String getName() {
      return "Remove offline users";
    }

    public boolean isEnabled() {
      return true;
    }

    public void execute() {
      myMediator.getUserList().removeOfflineUsers();
    }
    
  }
}
