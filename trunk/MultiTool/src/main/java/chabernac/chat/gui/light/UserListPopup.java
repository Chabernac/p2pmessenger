/*
 * Created on 10-mrt-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.chat.gui.light;

import java.awt.BorderLayout;

import chabernac.chat.gui.ChatMediator;
import chabernac.gui.GPanel;
import chabernac.gui.GPanelPopupMenu;

public class UserListPopup extends GPanelPopupMenu {
	private ChatMediator myMediator = null;

	public UserListPopup(GPanel aPanel, ChatMediator aModel) {
		super(aPanel);
		myMediator = aModel;
		buildGUI();
	}
	
	private void buildGUI(){
		setLayout(new BorderLayout());
		add(myMediator.getUserListPanel(), BorderLayout.NORTH);
	}
}
