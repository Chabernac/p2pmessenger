/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pclient.gui.action;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import chabernac.p2pclient.gui.ChatMediator;

public class ActionDecorator {
  private final JComponent myComponent;
  private final ChatMediator myMediator;
  
  public ActionDecorator(JComponent aComponent, ChatMediator aMediator){
    myComponent = aComponent;
    myMediator = aMediator;
  }
  
  public void decorate(int aCondition){
    ActionFactory theActionFactory = new ActionFactory(myMediator);
    
    InputMap theInputMap = myComponent.getInputMap(aCondition);

    theInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0), "previous");
    theInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0), "next");
    theInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "clear");
    theInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, InputEvent.CTRL_DOWN_MASK), "unlock");
    theInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "send");
    theInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_END, KeyEvent.ALT_DOWN_MASK), "last");
    theInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, KeyEvent.ALT_DOWN_MASK), "first");
    theInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, KeyEvent.ALT_DOWN_MASK), "delete");
    theInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.ALT_DOWN_MASK), "nextrow");
    theInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.ALT_DOWN_MASK), "replyall");
    theInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.ALT_DOWN_MASK), "reply");
    theInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, KeyEvent.SHIFT_DOWN_MASK), "clearusers");
    theInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAUSE, 0), "pause");
    theInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "undo");
    theInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), "redo");
    
    if(!(myComponent instanceof JTextArea)){
      for(char theChar=33;theChar<=126;theChar++){
        if(!(theChar == '+' || theChar == '-')){
          theInputMap.put(KeyStroke.getKeyStroke(theChar), "focusinput");
        }
      }
    }
    
    ActionMap theActionMap = myComponent.getActionMap();
    theActionMap.put("previous", new CommandAction(theActionFactory,ActionFactory.Action.PREVIOUS_MESSAGE ));
    theActionMap.put("next", new CommandAction(theActionFactory,ActionFactory.Action.NEXT_MESSAGE ));
    theActionMap.put("clear", new CommandAction(theActionFactory,ActionFactory.Action.CLEAR_MESSAGE));
    theActionMap.put("clearusers", new CommandAction(theActionFactory,ActionFactory.Action.CLEAR_USERS ));
    theActionMap.put("send", new CommandAction(theActionFactory,ActionFactory.Action.SEND_MESSAGE));
    theActionMap.put("first", new CommandAction(theActionFactory,ActionFactory.Action.FIRST_MESSAGE));
    theActionMap.put("last", new CommandAction(theActionFactory,ActionFactory.Action.LAST_MESSAGE));
    theActionMap.put("delete", new CommandAction(theActionFactory,ActionFactory.Action.DELETE_MESSAGE));
    theActionMap.put("reply", new CommandAction(theActionFactory,ActionFactory.Action.REPLY));
    theActionMap.put("replyall", new CommandAction(theActionFactory,ActionFactory.Action.REPLY_ALL));
    theActionMap.put("pause", new CommandAction(theActionFactory,ActionFactory.Action.PAUSE));
    theActionMap.put("undo", new CommandAction(theActionFactory,ActionFactory.Action.UNDO));
    theActionMap.put("redo", new CommandAction(theActionFactory,ActionFactory.Action.REDO));
    theActionMap.put("focusinput", new FocusInputAction(myMediator));
  }

}
