/*
 * Created on 19-feb-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.chat.gui.heavy;

import javax.swing.JPanel;

import chabernac.chat.gui.ChatGUIBuilder;
import chabernac.chat.gui.ChatMediator;
import chabernac.chat.gui.MessageField;
import chabernac.chat.gui.ReceivedMessagesField;
import chabernac.chat.gui.UserListPanel;

public class ChatGUIBuilderHeavy implements ChatGUIBuilder {
	
	public UserListPanel buildUserListPanel(ChatMediator aMediator) {
		return new UserListPanelHeavy(aMediator);
	}
	
	public MessageField buildMessageField(ChatMediator aMediator) {
		return new MessageField(aMediator);
	}
	
	public ReceivedMessagesField buildReceivedMessagesField(ChatMediator aMediator){
		return new ReceivedMessagesField(aMediator);
	}
	
	public JPanel buildChatPanel(ChatMediator aMediator) {
		return new ChatPanelHeavy(aMediator);
	}
	
}
