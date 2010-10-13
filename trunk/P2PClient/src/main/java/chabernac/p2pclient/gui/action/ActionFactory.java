package chabernac.p2pclient.gui.action;

import chabernac.command.Command;
import chabernac.p2pclient.gui.ChatMediator;

public class ActionFactory {
  private final ChatMediator myMediator;
  
  public static enum Action{PREVIOUS_MESSAGE,
                      NEXT_MESSAGE,
                      CLEAR_MESSAGE,
                      CLEAR_USERS,
                      SEND_MESSAGE,
                      REPLY,
                      REPLY_ALL,
                      FIRST_MESSAGE,
                      LAST_MESSAGE,
                      DELETE_MESSAGE,
                      FOCUS_INPUT_MESSAGE, 
                      UNDO,
                      REDO,
                      RECEIVE_CLOSED,
                      RECEIVE_AS_MESSAGE_INDICATES,
                      NO_POPUP,
                      TOGGLE_POPUP
                        };

  public ActionFactory(ChatMediator anMediator) {
    super();
    myMediator = anMediator;
  }
  
  public Command getCommand(Action anAction){
    if(anAction == Action.PREVIOUS_MESSAGE) return new PreviousMessageCommand(myMediator);
    if(anAction == Action.NEXT_MESSAGE) return new NextMessageCommand(myMediator);
    if(anAction == Action.CLEAR_MESSAGE) return new ClearCommand(myMediator);
    if(anAction == Action.SEND_MESSAGE) return new SendMessageCommand(myMediator);
    if(anAction == Action.REPLY) return new ReplyCommand(myMediator);
    if(anAction == Action.REPLY_ALL) return new ReplyAllCommand(myMediator);
    if(anAction == Action.FIRST_MESSAGE) return new FirstMessageCommand(myMediator);
    if(anAction == Action.LAST_MESSAGE) return new LastMessageCommand(myMediator);
    if(anAction == Action.DELETE_MESSAGE) return new DeleteMessageCommand(myMediator);
    if(anAction == Action.UNDO) return new UndoCommand();
    if(anAction == Action.REDO) return new RedoCommand();
    if(anAction == Action.RECEIVE_CLOSED) return new ReceiveClosedCommand();
    if(anAction == Action.RECEIVE_AS_MESSAGE_INDICATES) return new ReceiveAsMessageIndicatesCommand();
    if(anAction == Action.NO_POPUP) return new NoPopupCommand();
    if(anAction == Action.TOGGLE_POPUP) return new TogglePopupCommand();
    
    return null;
  }
}
