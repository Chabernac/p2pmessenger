package chabernac.p2pclient.gui.action;

import chabernac.p2pclient.gui.ChatMediator;

public class NextMessageCommand extends AbstractMediatorCommand {
  private static final long serialVersionUID = -5272770055123623782L;

  public NextMessageCommand(ChatMediator anMediator) {
    super(anMediator);
  }

  @Override
  public void execute() {
    myMediator.restoreNextMesssage();
  }

}
