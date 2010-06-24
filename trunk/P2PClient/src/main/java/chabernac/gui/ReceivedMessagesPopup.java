/*
 * Created on 12-mrt-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.gui;

import chabernac.command.AbstractCommand;
import chabernac.p2pclient.gui.ChatMediator;

public class ReceivedMessagesPopup extends GPanelPopupMenu {
  private static final long serialVersionUID = 420311445776884476L;
  private ChatMediator myMediator = null;

	public ReceivedMessagesPopup(GPanel aPanel, ChatMediator aMediator) {
		super(aPanel);
		myMediator = aMediator;
		buildMenu();
	}
	
	private void buildMenu(){
		add(new CommandMenuItem(new ClearCommand()));
	}
	
	private class ClearCommand extends AbstractCommand{
		public String getName() { return "Clear"; }
		public boolean isEnabled() { return true; }
		public void execute() { myMediator.clearReceivedMessages(); }
	}
}
