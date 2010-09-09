package chabernac.p2pclient.gui.action;

import chabernac.p2pclient.gui.ChatMediator;

public class LastMessageCommand extends AbstractMediatorCommand {
  private static final long serialVersionUID = -610125396515596673L;

  public LastMessageCommand(ChatMediator anMediator) {
    super(anMediator);
  }

  @Override
  public void execute() {
    myMediator.restoreLastMessage();
  }

}
