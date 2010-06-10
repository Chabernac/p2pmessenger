/*
 * Created on 19-feb-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.chat.gui;

import javax.swing.JPanel;

public interface ChatGUIBuilder {
	public UserListPanel buildUserListPanel(ChatMediator aMediator);
	public MessageField buildMessageField(ChatMediator aMediator);
	public ReceivedMessagesField buildReceivedMessagesField(ChatMediator aMediator);
	public JPanel buildChatPanel (ChatMediator aMediator);
}
