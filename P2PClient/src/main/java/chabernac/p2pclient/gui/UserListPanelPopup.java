/*
 * Created on 17-feb-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.p2pclient.gui;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import chabernac.command.AbstractCommand;
import chabernac.gui.CommandMenuItem;
import chabernac.gui.GPanelPopupMenu;
import chabernac.protocol.facade.P2PFacadeException;
import chabernac.protocol.userinfo.UserInfo.Status;

public class UserListPanelPopup extends GPanelPopupMenu {
  private static final long serialVersionUID = -4244200497297440427L;

  private static final Logger logger = Logger.getLogger(UserListPanelPopup.class);

  private UserPanel myPanel = null;
  private ChatMediator myMediator = null;
  private String lastSelectedGroup = "";

  public UserListPanelPopup(UserPanel aPanel, ChatMediator aMediator) throws P2PFacadeException{
    super(aPanel);
    myPanel = aPanel;
    myMediator = aMediator;
    buildMenu();
  }

  private void buildMenu(){
    removeAll();
    add(new CommandMenuItem(new ClearCommand()));
    add(new CommandMenuItem(new NewGroupCommand()));
    add(new CommandMenuItem(new AddToGroupCommand()));
    add(new CommandMenuItem(new RemoveGroupCommand()));
//    add(new CommandMenuItem(new RemoveOfflineUsers()));
    addSeparator();
    buildGroups();
    addSeparator();
//    add(new CommandMenuItem(new HideCommand()));
//    addSeparator();

    try{
      add(new CommandMenuItem(new StatusCommand(Status.ONLINE)));
      add(new CommandMenuItem(new StatusCommand(Status.AWAY)));
      add(new CommandMenuItem(new StatusCommand(Status.BUSY)));
      add(new CommandMenuItem(new StatusCommand(Status.OFFLINE)));
    }catch(P2PFacadeException e){
      logger.error( "Error occured while constructing menu", e );
    }
    addSeparator();
    add(new CommandMenuItem(new SearchPeersCommand()));

  }

  private void buildGroups(){
    for(Iterator<String> i=myPanel.getGroups().keySet().iterator();i.hasNext();){
      add(new CommandMenuItem(new SelectGroupCommand((String)i.next())));
    }
  }

  private class ClearCommand extends AbstractCommand{
    public String getName() { return "Wissen"; }
    public boolean isEnabled() { return true; }
    public void execute() { myPanel.setSelectedUsers( new HashSet< String >() ); }
  }

  private class NewGroupCommand extends AbstractCommand{
    public String getName() { return "Nieuwe groep"; }
    public boolean isEnabled() { return true; }
    public void execute() {

      //TODO implement Group
      String groupName = JOptionPane.showInputDialog(myPanel, "Naam van de groep");
      if(groupName == null || groupName.equals("")) return;
      myPanel.createGroupForSelectedUsers( groupName );
      buildMenu();
    }
  }

  private class AddToGroupCommand extends AbstractCommand{
    public String getName() { return "Toevoegen aan groep"; }
    public boolean isEnabled() { return true; }
    public void execute() {

      //TODO implement Group
      String groupName = JOptionPane.showInputDialog(myPanel, "Naam van de groep");
      if(groupName == null || groupName.equals("")) return;
      myPanel.addSelectedUsersToGroup( groupName );
      buildMenu();
    }
  }

  
  private class RemoveGroupCommand extends AbstractCommand{
    public String getName() { return "Groep verwijderen"; }
    public boolean isEnabled() { return true; }
    public void execute() {
      myPanel.removeGroup( lastSelectedGroup );
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
      myPanel.selectGroup(myGroup);
    }
  }
  
  private class SearchPeersCommand extends AbstractCommand{
    public String getName() { return "Zoeken naar peers"; }

    public boolean isEnabled() { return true; }

    public void execute(){
      try {
        myMediator.getP2PFacade().scanSuperNodes();
      } catch ( P2PFacadeException e ) {
        logger.error( "Unable to scan super nodes" );
      }
    }
  }

  private class HideCommand extends AbstractCommand{
    public String getName() {
      return "Hide to";
      //TODO implement hide
//      if(myMediator.getMessengerClientService().isHideTo()){
//        return "Unhide to";
//      } else {
//        return "Hide to";
//      }
    }

    public boolean isEnabled(){ return true;    }

    public void execute() {
      //TODO implement hide
//      myMediator.getMessengerClientService().setHideTo(!myMediator.getMessengerClientService().isHideTo());
//      notifyObs();
    }
  }

  private class StatusCommand extends AbstractCommand implements Observer{
    private Status myStatus = Status.ONLINE;
    private Status myCurrentStatus = Status.ONLINE;

    public StatusCommand(Status aStatus) throws P2PFacadeException{
      myStatus = aStatus;
      myMediator.getP2PFacade().getPersonalInfo().addObserver( this );
    }

    public String getName() {
      return ResourceBundle.getBundle( "resources" ).getString( myStatus.name() );
    }

    public boolean isEnabled() {
      return myStatus != myCurrentStatus;
    }

    public void execute() {
      try {
        myMediator.getP2PFacade().getPersonalInfo().setStatus( myStatus );
      } catch ( P2PFacadeException e ) {
        logger.error( "Unable to execute status command", e );
      }
    }

    @Override
    public void update( Observable anO, Object anArg ) {
      try {
        myCurrentStatus = myMediator.getP2PFacade().getPersonalInfo().getStatus();
        notifyObs();
      } catch ( P2PFacadeException e ) {
        logger.error( "Could not retrieve status", e );
      }
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
      //TODO implement this
    }

  }
}
