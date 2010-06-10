/*
 * Created on 17-feb-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.chat.gui.light;

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

public class ChatPanelLight extends JPanel implements iEventListener{
  private ChatMediator myMediator = null;
  private JSplitPane mySplitPane = null;

  public ChatPanelLight(ChatMediator aMediator){
    myMediator = aMediator;
    buildGUI();
    ApplicationEventDispatcher.addListener(this, ApplicationSaveEvent.class);
  }

  private void buildGUI(){
    setLayout(new BorderLayout());

    mySplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

    JTextArea theSendArea = myMediator.getMessageField();
    mySplitPane.setTopComponent(new JScrollPane(theSendArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
    mySplitPane.setBottomComponent(myMediator.getReceivedMessagesField());
    mySplitPane.setDividerSize(1);
    ApplicationPreferences thePreferences = ApplicationPreferences.getInstance();
    mySplitPane.setDividerLocation(Integer.parseInt(thePreferences.getProperty("chat.light.dividerlocation", "80")));

    add(mySplitPane, BorderLayout.CENTER);
    add(myMediator.getUserListPanel(), BorderLayout.SOUTH);
    /*
		if( Boolean.parseBoolean(ApplicationPreferences.getInstance().getProperty("chat.light.userpanelonleft")) ){
			add(myMediator.getUserListPanel(), BorderLayout.WEST);
		} else {
			new UserListPopup(theSendPanel, myMediator);
		}
     */
  }



  public void eventFired(Event evt) {
    savePreferences();
  }

  private void savePreferences(){
    ApplicationPreferences thePreferences = ApplicationPreferences.getInstance();
    thePreferences.setProperty("chat.light.dividerlocation", Integer.toString(mySplitPane.getDividerLocation()));
  }

}
