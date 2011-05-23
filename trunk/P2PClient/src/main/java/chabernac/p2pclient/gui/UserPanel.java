/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pclient.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import chabernac.gui.ComponentMoveDecorator;
import chabernac.gui.GPanel;
import chabernac.gui.LinePainter;
import chabernac.gui.WrappingFlowLayout;
import chabernac.gui.iComponentMoveListener;
import chabernac.gui.iPaintable;
import chabernac.io.Base64ObjectStringConverter;
import chabernac.p2pclient.gui.action.ActionDecorator;
import chabernac.preference.ApplicationPreferences;
import chabernac.protocol.facade.P2PFacadeException;
import chabernac.protocol.message.DeliveryReport;
import chabernac.protocol.message.MultiPeerMessage;
import chabernac.protocol.message.iDeliverReportListener;
import chabernac.protocol.routing.RoutingTableEntry;
import chabernac.protocol.userinfo.UserInfo;
import chabernac.protocol.userinfo.iUserInfoListener;
import chabernac.protocol.userinfo.UserInfo.Status;
import chabernac.tools.Tools;

public class UserPanel extends GPanel implements iUserSelectionProvider{
  private static Logger LOGGER = Logger.getLogger(UserPanel.class);
  private static final long serialVersionUID = 1482661062066245974L;
  protected final ChatMediator myMediator;
  protected Point myDragPoint = null;

  private Map<String, StatusCheckBox> myCheckBoxes = Collections.synchronizedMap( new LinkedHashMap< String, StatusCheckBox >());
  private List< Component > myCheckBoxesList = new ArrayList< Component >();
  private List<iSelectionChangedListener> mySelectionListeners = new ArrayList< iSelectionChangedListener >();
  private ChangeListener myCheckBoxListener = new MyCheckBoxListener();
  private Map<String, Set<String>> myGroups = new HashMap< String, Set<String> >();

  private iPaintable mySeperator = null;

  public UserPanel(ChatMediator aP2PFacade) throws P2PFacadeException{
    myMediator = aP2PFacade;
    loadGroupsInPreferences();
    init();
    buildGUI();
    addListener();
    buildActionMap();

  }

  private void init() throws P2PFacadeException{
    new UserListPanelPopup(this, myMediator);
  }

  private void buildGUI(){
    Tools.invokeLaterIfNotEventDispatchingThread( new Runnable(){
      public void run(){
        setLayout( new WrappingFlowLayout() );

        try {
          addUsers( myMediator.getP2PFacade().getUserInfo() );
        } catch ( P2PFacadeException e ) {
          LOGGER.error("Error occured while getting user info", e);
        } 
      }
    });
  }

  private void addListener() throws P2PFacadeException{
    myMediator.getP2PFacade().addUserInfoListener( new UserInfoListener() );
    myMediator.getP2PFacade().addDeliveryReportListener( new DeliveryReportListener() );
    new ComponentMoveDecorator(this, new ComponentMoveListener());
  }

  private void addUsers(final Map<String,  UserInfo > aUserList){
    Tools.invokeLaterIfNotEventDispatchingThread(  new Runnable(){
      public void run(){
        for(String thePeerId : aUserList.keySet()){
          try {
            if(!myMediator.getP2PFacade().getPeerId().equals( thePeerId )){

              if(!myCheckBoxes.containsKey( thePeerId )){
                StatusCheckBox theCheckBox = new StatusCheckBox(Status.ONLINE);
                theCheckBox.addChangeListener( myCheckBoxListener );
                int theFontSize = Integer.parseInt(ApplicationPreferences.getInstance().getProperty("userlist.font.size", "12"));
                theCheckBox.setFontSize( theFontSize );
                myCheckBoxes.put( thePeerId, theCheckBox );
                myCheckBoxesList.add( theCheckBox );
                add( theCheckBox );
              }
              StatusCheckBox theCheckBox = myCheckBoxes.get( thePeerId );
              modifyCheckBoxForUser(theCheckBox, aUserList.get( thePeerId ), thePeerId);
            }
          } catch ( P2PFacadeException e ) {
          }
        }
      }
    });
  }

  private void modifyCheckBoxForUser( StatusCheckBox anCheckBox, UserInfo anUserInfo, String aPeerId ) throws P2PFacadeException {
    anCheckBox.setText(  getLabelForUser(anUserInfo) );
    anCheckBox.setToolTipText( getToolTipForUser(aPeerId, anUserInfo) );
    anCheckBox.setForeground( getColorForStatus(anUserInfo) );
    anCheckBox.setStatus( anUserInfo.getStatus() );
  }

  public Map<String, Set<String>> getGroups(){
    return Collections.unmodifiableMap( myGroups );
  }

  public void createGroupForSelectedUsers(String aGroupName){
    myGroups.put( aGroupName, getSelectedUserIds());
    saveGroupsInPreferences();
  }
  
  public void addSelectedUsersToGroup( String anGroupName ) {
    if(!myGroups.containsKey( anGroupName )) createGroupForSelectedUsers( anGroupName );
    Set<String> theUsersInGroup = myGroups.get(anGroupName);
    theUsersInGroup.addAll( getSelectedUserIds() );
    saveGroupsInPreferences();
  }

  public void selectGroup(String aGroupName){
    if(myGroups.containsKey( aGroupName )){
      setSelectedUserIds( myGroups.get( aGroupName ) );
    }
  }

  public void removeGroup(String aGroupName){
    myGroups.remove( aGroupName );
    saveGroupsInPreferences();
  }

  private void saveGroupsInPreferences(){
    ApplicationPreferences thePreferences = ApplicationPreferences.getInstance();
    try {
      thePreferences.put( "userpanel.groups", new Base64ObjectStringConverter<Serializable>().toString( (Serializable) myGroups ));
    } catch ( IOException e ) {
      LOGGER.error("Could not save group in prefererences", e);
    }
  }

  private void loadGroupsInPreferences(){
    ApplicationPreferences thePreferences = ApplicationPreferences.getInstance();
    String theGroups = thePreferences.getProperty( "userpanel.groups" );
    if(theGroups != null && theGroups.length() > 0){
      try {
        myGroups = new Base64ObjectStringConverter<HashMap< String, Set< String > >>().getObject( theGroups );
      } catch ( IOException e ) {
        LOGGER.error( "Could not load groups in preferences", e );
      }
    }
  }



  private Color getColorForStatus( UserInfo anUserInfo ) {
    Status theStatus = Status.OFFLINE;
    if(anUserInfo != null){
      theStatus = anUserInfo.getStatus();
    }

    if(theStatus == Status.OFFLINE) return Color.GRAY;
    return new Color(0,0,200);
  }

  private String getToolTipForUser(String aPeerId, UserInfo anUserInfo ) throws P2PFacadeException {
    RoutingTableEntry theEntry = myMediator.getP2PFacade().getRoutingTableEntry( aPeerId );
    String theStatus = anUserInfo.getStatus().name();
    if(anUserInfo.getStatusMessage() != null) theStatus += " '" + anUserInfo.getStatusMessage() + "'";
    theStatus += " "
    + anUserInfo.getName()
    + " "
    + anUserInfo.getId() 
    + " " 
    + anUserInfo.getEMail() 
    + " '" + anUserInfo.getTelNr() 
    + "' [" + aPeerId + " " + theEntry.getHopDistance() 
    + " " 
    + theEntry.getPeer().getEndPointRepresentation() + "]";
    return theStatus;
  }

  private String getLabelForUser( UserInfo anUserInfo ) {
    String theUserLabel = Tools.getShortNameForUser( anUserInfo );
    if(theUserLabel == null || "".equals( theUserLabel )){
      theUserLabel = anUserInfo.getId();
    }
    return theUserLabel;
  }

  private void buildActionMap(){
    new ActionDecorator(this, myMediator).decorate(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    InputMap theMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    theMap.put(KeyStroke.getKeyStroke('+'), "largerfont");
    theMap.put(KeyStroke.getKeyStroke('-'), "smallerfont");
    theMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "removeuser");
    ActionMap theActionMap = getActionMap();
    theActionMap.put("largerfont", new FontChangeAction(true));
    theActionMap.put("smallerfont", new FontChangeAction(false));
  }

  public class UserInfoListener implements iUserInfoListener {
    public void userInfoChanged( UserInfo aUserInfo, Map<String,  UserInfo > aFullUserInfoList ) {
      addUsers(new HashMap< String, UserInfo >(aFullUserInfoList));
    }
  }

  private class FontChangeAction extends AbstractAction{
    private static final long serialVersionUID = -5719660142885695128L;
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
      for(Iterator<StatusCheckBox> i=myCheckBoxes.values().iterator();i.hasNext();){
        i.next().setFontSize(theFontSize);
      }
      revalidate();
    }
  }

  private void layoutCheckBoxes(){
    Tools.invokeLaterIfNotEventDispatchingThread(new Runnable(){
      public void run(){
        removeAll();
        Iterator<? extends Component> theIterator = myCheckBoxesList.iterator();
        while(theIterator.hasNext()){
          add(theIterator.next());
        }
        revalidate();
      }
    });
  }

  public void paint(Graphics g){
    super.paint(g);
    if(mySeperator != null){
      mySeperator.paint( g );
    }
  }

  public class ComponentMoveListener implements iComponentMoveListener {
    @Override
    public void componentDropped( Component aTarget, Component aSource, boolean isAfter ) {
      myCheckBoxesList.remove(aSource);
      int index = myCheckBoxesList.indexOf(aTarget);
      if(isAfter)   myCheckBoxesList.add(index + 1, aSource);
      else myCheckBoxesList.add(index, aSource);
      layoutCheckBoxes();
    }

    @Override
    public void drawSeperator( Component anComponent, boolean anInsertComponentAfter ) {
      int theLineXPosition;
      if(anInsertComponentAfter){
        theLineXPosition = anComponent.getX() + anComponent.getWidth();
      } else {
        theLineXPosition = anComponent.getX();
      }
      mySeperator = new LinePainter(theLineXPosition, anComponent.getY(), theLineXPosition, anComponent.getY() + anComponent.getHeight(), Color.gray);
      repaint();
    }

    @Override
    public void removeSeparator() {
      mySeperator = null;
      repaint();
    }
  }

  @Override
  public Set< String > getSelectedUsers() {
    Set<String> theSelectedUsers = new HashSet< String >();
    for(String thePeerId : myCheckBoxes.keySet()){
      if(myCheckBoxes.get( thePeerId ).isSelected()){
        theSelectedUsers.add(thePeerId);
      }
    }
    return theSelectedUsers;
  }

  public Set<String> getSelectedUserIds(){
    Set<String> theSelectedUsers = new HashSet< String >();
    try{
      Map< String, UserInfo > theUserInfoMap = myMediator.getP2PFacade().getUserInfo();
      for(String thePeerId : myCheckBoxes.keySet()){
        if(myCheckBoxes.get( thePeerId ).isSelected()){
          UserInfo theUserInfo = theUserInfoMap.get( thePeerId );
          if(theUserInfo != null){
            theSelectedUsers.add(theUserInfo.getId());
          }
        }
      }
    }catch(Exception e){
      LOGGER.error( "Could not get selected user id's " );
    }
    return theSelectedUsers;
  }

  @Override
  public void setSelectedUsers( Set< String > aUserList ) {
    clear();

    //now select the ones from the list
    for(String thePeerId : aUserList){
      selectedCheckBoxForUser( thePeerId );
    }
  }

  private void setSelectedUserIds( Set< String > aUserList ) {
    clear();

    //now select the ones from the list
    for(String theUserId : aUserList){
      Set<String> theUsersWithId = findUsersForUserId( theUserId );

      for(String thePeerId : theUsersWithId){
        selectedCheckBoxForUser( thePeerId );
      }
    }
  }

  private void selectedCheckBoxForUser(String aPeerId){
    try{
      UserInfo theUserInfo = myMediator.getP2PFacade().getUserInfo().get( aPeerId );
      JCheckBox theCheckBox = myCheckBoxes.get(aPeerId);
      if(theCheckBox == null) return;
      if(theUserInfo == null || theUserInfo.getStatus() == Status.OFFLINE){
        theCheckBox.setSelected( false );
      } else {
        theCheckBox.setSelected( true );
      }
    }catch(Exception e){
      LOGGER.error("Could not select checkbox for user '" + aPeerId + "'");
    }
  }

  private Set<String> findUsersForUserId(String aUserId){
    Set< String > theUsers = new HashSet< String >();
    try{
      Map< String, UserInfo > theUserInfo = myMediator.getP2PFacade().getUserInfo();
      for(String thePeerId : theUserInfo.keySet()){
        UserInfo theUser = theUserInfo.get(thePeerId);
        if(theUser.getId().equalsIgnoreCase( aUserId )){
          theUsers.add( thePeerId );
        }
      }
    }catch(Exception e){
      LOGGER.error("Could not find users for user id '" + aUserId + "'");
    }
    return theUsers;
  }

  private void clearColors() throws P2PFacadeException{
    for(String thePeerd : myCheckBoxes.keySet()){
      UserInfo theUserInfo = myMediator.getP2PFacade().getUserInfo().get( thePeerd );
      myCheckBoxes.get(thePeerd).setForeground( getColorForStatus( theUserInfo ) );
    }
  }

  public void setMultiPeerMessage(MultiPeerMessage aMessage){
    //first clear all the colors to the defaults
    try{
      clearColors();

      Map<String, DeliveryReport> theReports = myMediator.getP2PFacade().getMessageArchive().getDeliveryReportsForMultiPeerMessage( aMessage );
      for(String thePeerId : theReports.keySet()){
        if(myCheckBoxes.containsKey( thePeerId )){
          myCheckBoxes.get( thePeerId ).setDeliveryStatus( theReports.get( thePeerId ).getDeliveryStatus() );
        }
      }
    }catch(P2PFacadeException e){
      LOGGER.error("Error occured while setting multi peer message", e);
    }
  }

  Map< String, StatusCheckBox > getCheckBoxes() {
    return myCheckBoxes;
  }

  List< Component > getCheckBoxesList() {
    return myCheckBoxesList;
  }

  @Override
  public void clear() {
    for(JCheckBox theCheckBox : myCheckBoxes.values()){
      theCheckBox.setSelected( false );
    }
  }

  private class DeliveryReportListener implements iDeliverReportListener {

    @Override
    public void acceptDeliveryReport( DeliveryReport aDeliverReport ) {
      if(myMediator.getLastSendMessage().getUniqueId().equals( aDeliverReport.getMultiPeerMessage().getUniqueId())){
        setMultiPeerMessage( aDeliverReport.getMultiPeerMessage() );
      }
    }
  }

  @Override
  public void addSelectionChangedListener( iSelectionChangedListener aListener ) {
    mySelectionListeners.add( aListener );
  }

  private class MyCheckBoxListener implements ChangeListener {
    @Override
    public void stateChanged( ChangeEvent aE ) {
      for(iSelectionChangedListener theListener : mySelectionListeners){
        theListener.selectionChanged();
      }
    }
  }
}
