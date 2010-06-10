/*
 * Created on 17-feb-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.chat.gui.heavy;

import javax.swing.JFrame;

import chabernac.chat.gui.ChatMediator;
import chabernac.messengerservice.MessengerClientService;
import chabernac.task.gui.MainFrame;

public class HeavyChatMediator extends ChatMediator {

	public HeavyChatMediator(MessengerClientService aModel, JFrame aFrame) {
		super(aModel, aFrame, new ChatGUIBuilderHeavy());
	}
	
	protected void focusOnFrame(JFrame aFrame){
		if(aFrame instanceof MainFrame){
			((MainFrame)aFrame).focusOnChatTab();
		}
	}

}
