/*
 * Created on 17-feb-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.chat.gui.heavy;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import chabernac.chat.gui.ChatMediator;
import chabernac.event.ApplicationEventDispatcher;
import chabernac.event.Event;
import chabernac.event.iEventListener;
import chabernac.preference.ApplicationPreferences;
import chabernac.task.event.ApplicationSaveEvent;

public class ChatPanelHeavy extends JPanel implements iEventListener{
  private ChatMediator myMediator = null;
  private JSplitPane mySplitPane = null;

  public ChatPanelHeavy(ChatMediator aMediator){
    myMediator = aMediator;
    buildGUI();
    ApplicationEventDispatcher.addListener(this, ApplicationSaveEvent.class);
  }


  private void buildGUI(){
    setLayout(new BorderLayout());

    mySplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    JTextArea theSendArea = myMediator.getMessageField();
    mySplitPane.setTopComponent(new JScrollPane(theSendArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
    //mySplitPane.setBottomComponent(myReceiveScrollPane);
    mySplitPane.setBottomComponent(myMediator.getReceivedMessagesField());


    add(mySplitPane, BorderLayout.CENTER);
    add(myMediator.getUserListPanel(), BorderLayout.WEST);
    mySplitPane.setDividerSize(1);

    ApplicationPreferences thePreferences = ApplicationPreferences.getInstance();
    mySplitPane.setDividerLocation(Integer.parseInt(thePreferences.getProperty("chat.heavy.dividerlocation", "100")));
  }

  public void eventFired(Event evt) {
    savePreferences();
  }

  private void savePreferences(){
    ApplicationPreferences thePreferences = ApplicationPreferences.getInstance();
    thePreferences.setProperty("chat.heavy.dividerlocation", Integer.toString(mySplitPane.getDividerLocation()));
  }
}
