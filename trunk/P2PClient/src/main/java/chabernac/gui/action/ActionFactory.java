package chabernac.gui.action;

import chabernac.command.Command;
import chabernac.p2pclient.gui.ChatMediator;

public class ActionFactory {
  private final ChatMediator myMediator;
  
  private enum Action{PREVIOUS_MESSAGE,
                      NEXT_MESSAGE,
                      CLEAR,
                      SEND_MESSAGE};

  public ActionFactory(ChatMediator anMediator) {
    super();
    myMediator = anMediator;
  }
  
  public Command getCommand(Action anAction){
    if(anAction == Action.PREVIOUS_MESSAGE) return new PreviousMessageAction(myMediator);
    if(anAction == Action.NEXT_MESSAGE) return new NextMessageAction(myMediator);
    if(anAction == Action.CLEAR) return new ClearMessageAction(myMediator);
    if(anAction == Action.SEND_MESSAGE) return new SendMessageCommand(myMediator);
    
    return null;
  }
}
