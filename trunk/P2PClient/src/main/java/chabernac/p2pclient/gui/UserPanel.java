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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;

import chabernac.GUI.WrappingFlowLayout;
import chabernac.gui.ComponentMoveDecorator;
import chabernac.gui.GPanel;
import chabernac.gui.LinePainter;
import chabernac.gui.iComponentMoveListener;
import chabernac.gui.iPaintable;
import chabernac.preference.ApplicationPreferences;
import chabernac.protocol.facade.P2PFacade;
import chabernac.protocol.facade.P2PFacadeException;
import chabernac.protocol.userinfo.UserInfo;
import chabernac.protocol.userinfo.iUserInfoListener;
import chabernac.protocol.userinfo.UserInfo.Status;
import chabernac.tools.Tools;

public class UserPanel extends GPanel implements iUserSelectionProvider{
  private static Logger LOGGER = Logger.getLogger(UserPanel.class);
  private static final long serialVersionUID = 1482661062066245974L;
  protected final P2PFacade myP2PFacade;
  protected Point myDragPoint = null;
  
  private Map<String, StatusCheckBox> myCheckBoxes = Collections.synchronizedMap( new LinkedHashMap< String, StatusCheckBox >());
  private List< Component > myCheckBoxesList = new ArrayList< Component >();
  
  private iPaintable mySeperator = null;

  public UserPanel(P2PFacade aP2PFacade) throws P2PFacadeException{
    myP2PFacade = aP2PFacade;
    init();
    buildGUI();
    addListener();
    buildActionMap();
  }
  
  private void init() throws P2PFacadeException{
    new UserListPanelPopup(this, myP2PFacade);
  }

  private void buildGUI(){
    Tools.invokeLaterIfNotEventDispatchingThread( new Runnable(){
      public void run(){
        setLayout( new WrappingFlowLayout() );
        
        try {
          addUsers( myP2PFacade.getUserInfo() );
        } catch ( P2PFacadeException e ) {
          LOGGER.error("Error occured while getting user info", e);
        } 
      }
    });
  }

  private void addListener() throws P2PFacadeException{
    myP2PFacade.addUserInfoListener( new UserInfoListener() );
    new ComponentMoveDecorator(this, new ComponentMoveListener());
  }

  private void addUsers(final Map<String,  UserInfo > aUserList){
    Tools.invokeLaterIfNotEventDispatchingThread(  new Runnable(){
      public void run(){
        for(String thePeerId : aUserList.keySet()){
          if(!myCheckBoxes.containsKey( thePeerId )){
            StatusCheckBox theCheckBox = new StatusCheckBox(Status.ONLINE);
            myCheckBoxes.put( thePeerId, theCheckBox );
            myCheckBoxesList.add( theCheckBox );
            add( theCheckBox );
          }
          StatusCheckBox theCheckBox = myCheckBoxes.get( thePeerId );
          modifyCheckBoxForUser(theCheckBox, aUserList.get( thePeerId ));
        }
      }
    });
  }
  
  private void modifyCheckBoxForUser( StatusCheckBox anCheckBox, UserInfo anUserInfo ) {
    anCheckBox.setText(  getLabelForUser(anUserInfo) );
    anCheckBox.setToolTipText( getToolTipForUser(anUserInfo) );
    anCheckBox.setForeground( getColorForStatus(anUserInfo) );
    anCheckBox.setStatus( anUserInfo.getStatus() );
  }

  private Color getColorForStatus( UserInfo anUserInfo ) {
    Status theStatus = anUserInfo.getStatus();
    
    if(theStatus == Status.OFFLINE) return Color.GRAY;
    return new Color(0,200,0);
  }

  private String getToolTipForUser( UserInfo anUserInfo ) {
    return anUserInfo.getId() + " " + anUserInfo.getEMail() + " " + anUserInfo.getTelNr();
  }

  private String getLabelForUser( UserInfo anUserInfo ) {
    String theUserLabel = anUserInfo.getName();
    if(theUserLabel == null || "".equals( theUserLabel )){
      theUserLabel = anUserInfo.getId();
    }
    return theUserLabel;
  }
  
  private void buildActionMap(){
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
      addUsers(aFullUserInfoList);
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
  public List< String > getSelectedUsers() {
    List<String> theSelectedUsers = new ArrayList< String >();
    for(String thePeerId : myCheckBoxes.keySet()){
      if(myCheckBoxes.get( thePeerId ).isSelected()){
        theSelectedUsers.add(thePeerId);
      }
    }
    return theSelectedUsers;
  }

  @Override
  public void setSelectedUsers( List< String > aUserList ) {
    //fist clear all checkboxes
    for(JCheckBox theCheckBox : myCheckBoxes.values()){
      theCheckBox.setSelected( false );
    }
    
    //now select the ones from the list
    for(String thePeerId : aUserList){
      JCheckBox theCheckBox = myCheckBoxes.get( thePeerId );
      if(theCheckBox != null){
        theCheckBox.setSelected( true );
      }
    }
  }
  
  Map< String, StatusCheckBox > getCheckBoxes() {
    return myCheckBoxes;
  }

  List< Component > getCheckBoxesList() {
    return myCheckBoxesList;
  }
}
