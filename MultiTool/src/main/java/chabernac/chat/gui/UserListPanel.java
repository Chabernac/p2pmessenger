/*
 * Created on 6-jan-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.chat.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import chabernac.chat.Message;
import chabernac.chat.gui.event.SelectUsersEvent;
import chabernac.chat.gui.event.TotalUserListChangedEvent;
import chabernac.event.ApplicationEventDispatcher;
import chabernac.event.Event;
import chabernac.event.iEventListener;
import chabernac.gui.GPanel;
import chabernac.io.IOOperator;
import chabernac.messengerservice.MessengerUser;
import chabernac.messengerservice.event.MessageSelectedEvent;
import chabernac.messengerservice.event.MessageSendEvent;
import chabernac.messengerservice.event.MessageStatusEvent;
import chabernac.preference.ApplicationPreferences;
import chabernac.task.event.ApplicationSaveEvent;
import chabernac.util.Tools;


public abstract class UserListPanel extends GPanel implements iEventListener{
//  private static final Logger LOGGER = Logger.getLogger(UserListPanel.class);
  private static final Color USER_UNKNWON = new Color(0,0,0);
  private static final Color USER_ALIVE = new Color(2,160,80);
  private static final Color NOT_DELIVERED = new Color(200,50,50);
  private static final Color NOT_YET_SEND = new Color(200,150,150);
  private static final Color SEND = new Color(50,50,200);
  private static final String  SEQUENCE_FILE = "sequence.bin";

  private ChatMediator myMediator = null;
  private Container myUsersPanel = null;
  private TreeMap myCheckBoxes = null;
  private HashMap myInvertedMap = null;
  private HashMap mySequenceMap = null;
  private ArrayList myCheckBoxesList = null;
  private UserListPanelPopup myPopup = null;
  private CheckBoxObservable myObservable = null;
  private boolean statusCleared = false;
  protected Point myDragPoint = null;
  private Point myLatestMouseLocation = null;

  public UserListPanel(ChatMediator aMediator){
    myMediator = aMediator;
    init();
    loadSequence();
    Tools.invokeLaterIfNotEventDispatchingThread(new Runnable(){
      public void run(){
        buildGUI();
        buildActionMap();
        addListeners();
      }
    });
  }

  private void init(){
    myCheckBoxes = new TreeMap();
    mySequenceMap = new HashMap();
    myPopup = new UserListPanelPopup(this, myMediator);
    myObservable = new CheckBoxObservable();
  }

  private void buildGUI(){
    myUsersPanel = createAndPlaceUserPanel();
    makeAndPlaceCheckBoxes();
    setFocusable(true);
  }

  private void buildActionMap(){
    InputMap theMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    theMap.put(KeyStroke.getKeyStroke('+'), "largerfont");
    theMap.put(KeyStroke.getKeyStroke('-'), "smallerfont");
    theMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "removeuser");
    ActionMap theActionMap = getActionMap();
    theActionMap.put("largerfont", new FontChangeAction(true));
    theActionMap.put("smallerfont", new FontChangeAction(false));
    theActionMap.put("removeuser", new RemoveUserAction());
  }

  public void addObserver(Observer anObserver){
    myObservable.addObserver(anObserver);
  }

  public void removeObserver(Observer anObserver){
    myObservable.deleteObserver(anObserver);
  }

  protected abstract Container createAndPlaceUserPanel();

  private void addListeners(){
    //myMediator.getUserList().addObserver(new UserObserver());
    
    //myMediator.getMessageArchive().addObserver(new MyMessageObserver());

    ComponentDragListener theDragListener = new ComponentDragListener();
    addGlobalMouseListener(theDragListener);
    addGlobalMouseMotionListener(theDragListener);
    ApplicationEventDispatcher.addListener(this, new Class[]{ApplicationSaveEvent.class, MessageStatusEvent.class, MessageSendEvent.class, SelectUsersEvent.class});
    ApplicationEventDispatcher.addListener(new MessageSelectedEventListener(), MessageSelectedEvent.class);
    ApplicationEventDispatcher.addListener(new TotalUserListChangedEventListener(), TotalUserListChangedEvent.class);
  }

  private void makeAndPlaceCheckBoxes(){
    createCheckBoxes();
    layoutCheckBoxes();
  }

  public JPopupMenu getPopup(){
    return myPopup;
  }

  private void createCheckBoxes(){
    myCheckBoxes.clear();
    if(myCheckBoxesList != null) fillSequenceMap();

    for(Iterator i=myMediator.getUserList().getUsers().values().iterator();i.hasNext();){
      MessengerUser theUser = (MessengerUser)i.next();
      //LOGGER.debug("Creating checkbox for: " + theUser.getUserName());
      StatusCheckBox theCheckBox = new StatusCheckBox();
      theCheckBox.setText(theUser.getShortName());
      myCheckBoxes.put(theUser.getId(), theCheckBox);
      theCheckBox.setToolTipText( theUser.toString() );
      theCheckBox.setStatus(theUser.getStatus());
      if(theUser.getStatus() == MessengerUser.ONLINE){
        theCheckBox.setForeground(USER_ALIVE);
      }
      if(theUser.getStatus() == MessengerUser.OFFLINE){
        theCheckBox.setEnabled(false);
        theCheckBox.setFocusable(true);
      }
    }

    for(Iterator i = myCheckBoxes.values().iterator(); i.hasNext();){
      ((JCheckBox)i.next()).addItemListener(new ItemListener(){
        public void itemStateChanged(ItemEvent evt){
          myObservable.notifyObs();
        }
      });
    }
    myInvertedMap = Tools.invertMap(myCheckBoxes);
    fillCheckBoxList();
  }

  private void fillCheckBoxList(){
    Collection theCollection = myCheckBoxes.values();
    myCheckBoxesList = new ArrayList(theCollection); 
    Collections.sort(myCheckBoxesList, new CheckBoxSorter());
  }

  private void fillSequenceMap(){
    Iterator theIterator = myCheckBoxesList.iterator();
    while(theIterator.hasNext()){
      Object theCheckBox = theIterator.next();
      String theUser = (String)myInvertedMap.get(theCheckBox);
      mySequenceMap.put(theUser, new Integer(myCheckBoxesList.indexOf(theCheckBox)));
    }
  }

  private void layoutCheckBoxes(){
    Tools.invokeLaterIfNotEventDispatchingThread(new Runnable(){
      public void run(){
        myUsersPanel.removeAll();
        Iterator theIterator = myCheckBoxesList.iterator();
        while(theIterator.hasNext()){
          JCheckBox theBox = (JCheckBox)theIterator.next();
          myUsersPanel.add(theBox);
        }
        revalidate();
      }
    });
  }



  private class CheckBoxSorter implements Comparator{

    public int compare(Object o1, Object o2) {
      if(!(o1 instanceof JCheckBox)) return 0;
      if(!(o2 instanceof JCheckBox)) return 0;
      JCheckBox theBox1 = (JCheckBox)o1;
      JCheckBox theBox2 = (JCheckBox)o2;

      String theUser1 = (String)myInvertedMap.get(theBox1);
      String theUser2 = (String)myInvertedMap.get(theBox2);

      int theValue1 = mySequenceMap.containsKey(theUser1) ? ((Integer)mySequenceMap.get(theUser1)).intValue() : 0;
      int theValue2 = mySequenceMap.containsKey(theUser2) ? ((Integer)mySequenceMap.get(theUser2)).intValue() : 0;

      if(theValue1 == 0 && theValue2 == 0) return theBox1.getText().compareTo(theBox2.getText());
      else return theValue1 - theValue2;
    }

  }

  public void clearStatuses(){
    Tools.invokeLaterIfNotEventDispatchingThread(new Runnable(){
      public void run(){
        statusCleared = true;
        Iterator theIterator = myCheckBoxes.values().iterator();
        while(theIterator.hasNext()){
          ((JCheckBox)theIterator.next()).setForeground(USER_UNKNWON);
        } 

        for(Iterator i=myMediator.getUserList().getUsers().values().iterator();i.hasNext();){
          MessengerUser theUser = (MessengerUser)i.next();
          if(theUser.getStatus() == MessengerUser.ONLINE && myCheckBoxes.containsKey(theUser.getId())){
            ((JCheckBox)myCheckBoxes.get(theUser.getId())).setForeground(USER_ALIVE);
          }
        }
      }
    });
  }

  public void clearCheckBoxes(){
    //LOGGER.debug("Clearing check boxes");
    Tools.invokeLaterIfNotEventDispatchingThread(new Runnable(){
      public void run(){
        Iterator theIterator = myCheckBoxes.values().iterator();
        while(theIterator.hasNext()){
          ((JCheckBox)theIterator.next()).setSelected(false);
        }
      }
    });
  }

  public void clear(){
    Tools.invokeLaterIfNotEventDispatchingThread(new Runnable(){
      public void run(){
        clearCheckBoxes();
        clearStatuses();
      }
    });
  }

  public ArrayList getSelectedUsers(){
    ArrayList theUsers = new ArrayList();
    String theUser = null;
    for(Iterator i = myCheckBoxes.keySet().iterator();i.hasNext();){
      theUser = (String)i.next();
      if( ((JCheckBox)myCheckBoxes.get(theUser)).isSelected()) {
        theUsers.add(theUser);
      }
    }
    return theUsers;
  }

  private void setSelectedUsers(final List aList){
    //LOGGER.debug("Selecting users of array: " + aList);
    Tools.invokeLaterIfNotEventDispatchingThread(new Runnable(){
      public void run(){
        clearCheckBoxes();
        String theUser = null;
        for(int i=0;i<aList.size();i++){
          theUser = (String)aList.get(i);
          if(myCheckBoxes.containsKey(theUser)){
            JCheckBox theCheckBox = (JCheckBox)myCheckBoxes.get(theUser);
            theCheckBox.setSelected(theCheckBox.isEnabled());
          }
        }    
      }
    });
    
  }


  /*

  private void selectUser(final String aUser){
    Tools.invokeLaterIfNotEventDispatchingThread(new Runnable(){
      public void run(){
        //LOGGER.debug("Selecting 1 user: " + aUser);
        if(myCheckBoxes.containsKey(aUser)){
          JCheckBox theCheckBox = ((JCheckBox)myCheckBoxes.get(aUser));
          theCheckBox.setSelected(theCheckBox.isEnabled());
        }

      }
    });
  }
  */



  private void checkUsers(final Message aMessage){
    Tools.invokeLaterIfNotEventDispatchingThread(new Runnable(){
      public void run(){
        //LOGGER.debug("Checking users for message: " + aMessage);
        for(Iterator i=myCheckBoxes.keySet().iterator();i.hasNext();){
          String theUser = (String)i.next();
          JCheckBox theCheckBox = (JCheckBox)myCheckBoxes.get(theUser);
//          if(aMessage != null) LOGGER.debug("Status of message for user: " + theUser + "= " + aMessage.getStatus(theUser));
          if(aMessage == null || !aMessage.getTo().contains(theUser) || aMessage.getStatus(theUser).equals(Message.NOT_SEND)){
//            LOGGER.debug("Deselecting check box for user: " + theUser);
            theCheckBox.setSelected(false);
            if(myMediator.getUserList().getUser(theUser).getStatus() == MessengerUser.ONLINE){ 
              theCheckBox.setForeground(USER_ALIVE);
            } else {
              theCheckBox.setForeground(USER_UNKNWON);
            }
          } else {
//            LOGGER.debug("Selecing check box for user: " + theUser);
            theCheckBox.setSelected(theCheckBox.isEnabled());
            if(aMessage.getStatus(theUser).equals(Message.FAILED) ){
              theCheckBox.setForeground(NOT_DELIVERED);
            } else if(aMessage.getStatus(theUser).equals(Message.SEND_IN_PROGRES) ){
              theCheckBox.setForeground(NOT_YET_SEND);
            } else if(aMessage.getStatus(theUser).equals(Message.SEND) ){
              theCheckBox.setForeground(SEND);
            }
          }
        }
        statusCleared = false;
        /*
         clear();
         statusCleared = false;
         if(aMessage != null){
         for(int i=0;i<aMessage.getTo().size();i++){
         String who = (String)aMessage.getTo().get(i);
         if(myCheckBoxes.containsKey(who)){
         JCheckBox theCheckBox = (JCheckBox)myCheckBoxes.get(who);
         theCheckBox.setSelected(true);
         //if(myMessage.getStatus(who).equals(Message.FAILED) && !myChatModel.getProtocol().getIdentifyMap().getAllAlive().containsKey(who)){
          if(aMessage.getStatus(who).equals(Message.FAILED) ){
          theCheckBox.setForeground(NOT_DELIVERED);
          } else if(aMessage.getStatus(who).equals(Message.SEND_IN_PROGRES) ){
          theCheckBox.setForeground(NOT_YET_SEND);
          } else if(aMessage.getStatus(who).equals(Message.SEND) ){
          theCheckBox.setForeground(SEND);
          }
          }
          }
          }
         */
      }
    });

  }

  private class TotalUserListChangedEventListener implements iEventListener{

    public void eventFired(Event anEvt) {
      Tools.invokeLaterIfNotEventDispatchingThread(new Runnable(){
        public void run(){
          ArrayList theSelectedUsers = getSelectedUsers();
          makeAndPlaceCheckBoxes();
          if(!statusCleared) checkSelectedMessage();
          setSelectedUsers(theSelectedUsers);
          repaint();
        }
      });
    }
  }

  private void checkSelectedMessage(){
    Message theMessage = myMediator.getMessageArchive().getSelectedMessage();
//    if(theMessage == null && myMediator.getMessageArchive().getSendMessages().size() > 0){
//      theMessage = (Message)myMediator.getMessageArchive().getSendMessages().get(myMediator.getMessageArchive().getSendMessages().size() - 1);
//    }
    checkUsers(theMessage);
  }


  private class MessageSelectedEventListener implements iEventListener{

    public void eventFired(Event anEvt) {
     checkSelectedMessage();
    }

  }

  private class CheckBoxObservable extends Observable{
    public void notifyObs(){
      setChanged();
      notifyObservers();
    }
  }

  protected abstract boolean insertComponentAfter(Point aPoint);

  private class ComponentDragListener implements MouseListener, MouseMotionListener{

    public Point getRelativePoint(MouseEvent evt){
      return SwingUtilities.convertPoint(evt.getComponent(), evt.getX(), evt.getY(), UserListPanel.this);
    }

    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {
      Point theRelativePoint = getRelativePoint(e);
      Component theSourceComponent = e.getComponent();
      Component theTargetComponent = SwingUtilities.getDeepestComponentAt(UserListPanel.this, theRelativePoint.x, theRelativePoint.y);

      if(!(theSourceComponent == null || 
          theTargetComponent == null || 
          !(theSourceComponent instanceof JCheckBox) ||
          !(theTargetComponent instanceof JCheckBox) ||
          theSourceComponent == theTargetComponent)){
        myCheckBoxesList.remove(theSourceComponent);
        int index = myCheckBoxesList.indexOf(theTargetComponent);
        if(insertComponentAfter(theRelativePoint))   myCheckBoxesList.add(index + 1, theSourceComponent);
        else myCheckBoxesList.add(index, theSourceComponent);
        layoutCheckBoxes();
      }
      myDragPoint = null;
      repaint();
    }

    public void mouseDragged(MouseEvent e) {
      myDragPoint = getRelativePoint(e);
      repaint();
    }

    public void mouseMoved(MouseEvent e) {
      myLatestMouseLocation = getRelativePoint(e);
    }

  }

  public void eventFired(Event evt){
    if(evt instanceof ApplicationSaveEvent){
      saveSequence();
    } else if(evt instanceof MessageSendEvent){
      checkUsers(((MessageSendEvent)evt).getMessage());
    } else if(evt instanceof MessageStatusEvent){
      //LOGGER.debug("Message status event received");
      Message theMessage = ((MessageStatusEvent)evt).getMessage();
      
      if(myMediator.getMessengerClientService().getLastMessageSend() == theMessage){
        checkUsers(theMessage);
      }
    } else if(evt instanceof SelectUsersEvent){
      setSelectedUsers(((SelectUsersEvent)evt).getUserList());
    }
  }

  private void saveSequence(){
    fillSequenceMap();
    IOOperator.saveObject(mySequenceMap, new File(SEQUENCE_FILE));
  }

  private void loadSequence(){
    Object theSequenceMap = IOOperator.loadObject(new File(SEQUENCE_FILE));
    if(theSequenceMap != null){
      mySequenceMap = (HashMap)theSequenceMap;
    }
  }

  private class FontChangeAction extends AbstractAction{
    private boolean isGrow = false;

    public FontChangeAction(boolean grow){
      isGrow = grow;
    }

    public void actionPerformed(ActionEvent e) {
      ApplicationPreferences thePreferences = ApplicationPreferences.getInstance();
      int theFontSize = Integer.parseInt(thePreferences.getProperty("userlist.font.size", "12"));
      if(isGrow) theFontSize++;
      else if(theFontSize > 5) theFontSize--;
      thePreferences.setProperty("userlist.font.size", Integer.toString(theFontSize));
      for(Iterator i=myCheckBoxesList.iterator();i.hasNext();){
        ((StatusCheckBox)i.next()).setFontSize(theFontSize);
      }
      revalidate();
    }
  }

  private class RemoveUserAction extends AbstractAction{

    public void actionPerformed(ActionEvent e) {
      final Component theComponent = SwingUtilities.getDeepestComponentAt(UserListPanel.this, myLatestMouseLocation.x, myLatestMouseLocation.y);
      if(theComponent instanceof StatusCheckBox){
        Tools.invokeLaterIfNotEventDispatchingThread(new Runnable(){
          public void run(){
            String theUser = (String)myInvertedMap.get(theComponent);
            myMediator.getUserList().removeUser(theUser);
            makeAndPlaceCheckBoxes();
            repaint();
          }
        });
      }
    }

  }


}
