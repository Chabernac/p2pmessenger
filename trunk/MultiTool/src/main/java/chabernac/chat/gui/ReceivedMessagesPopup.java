/*
 * Created on 12-mrt-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.chat.gui;

import chabernac.command.AbstractCommand;
import chabernac.gui.CommandMenuItem;
import chabernac.gui.GPanel;
import chabernac.gui.GPanelPopupMenu;
import chabernac.messengerservice.MessageArchive;

public class ReceivedMessagesPopup extends GPanelPopupMenu {
	private MessageArchive myModel = null;

	public ReceivedMessagesPopup(GPanel aPanel, MessageArchive aModel) {
		super(aPanel);
		myModel = aModel;
		buildMenu();
	}
	
	private void buildMenu(){
		add(new CommandMenuItem(new ClearCommand()));
	}
	
	private class ClearCommand extends AbstractCommand{
		public String getName() { return "Clear"; }
		public boolean isEnabled() { return true; }
		public void execute() { myModel.clearReceived(); }
	}
	
	

}
