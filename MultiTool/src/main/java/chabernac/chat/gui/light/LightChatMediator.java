/*
 * Created on 17-feb-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.chat.gui.light;

import javax.swing.JFrame;

import chabernac.chat.gui.ChatMediator;
import chabernac.messengerservice.MessengerClientService;

public class LightChatMediator extends ChatMediator {

	public LightChatMediator(MessengerClientService aModel, JFrame aFrame) {
		super(aModel, aFrame, new ChatGUIBuilderLight());
	}
}
