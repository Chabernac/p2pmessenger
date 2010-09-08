package chabernac.gui.action;

import chabernac.command.Command;
import chabernac.p2pclient.gui.ChatMediator;

public abstract class AbstractMediatorCommand implements Command{
  private static final long serialVersionUID = 2001984483649744275L;
  protected final ChatMediator myMediator;

  public AbstractMediatorCommand(ChatMediator anMediator) {
    super();
    myMediator = anMediator;
  }
}
