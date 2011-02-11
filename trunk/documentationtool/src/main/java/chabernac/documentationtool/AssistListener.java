package chabernac.documentationtool;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import chabernac.command.CommandSession;
import chabernac.documentationtool.command.NewArtifactCommand;
import chabernac.documentationtool.command.SaveCommand;

public class AssistListener implements KeyListener {
  private final DocumentationArea myTextComponent;
  private final DocumentationToolMediator myMediator;

  public AssistListener(DocumentationToolMediator aMediator, DocumentationArea aComponent) {
    myMediator = aMediator;
    myTextComponent = aComponent;
  }

  @Override
  public void keyPressed(KeyEvent anEvent) {
    if(anEvent.getKeyCode() == KeyEvent.VK_SPACE && anEvent.isControlDown()){
      myMediator.getAssistFrame().assist(myTextComponent.getArtifact(), myTextComponent.getCaretPosition());
    } else if(anEvent.getKeyCode() == KeyEvent.VK_S && anEvent.isControlDown()){
      CommandSession.getInstance().execute( new SaveCommand( myMediator ) );
    } else if(anEvent.getKeyCode() == KeyEvent.VK_N && anEvent.isControlDown()){
      CommandSession.getInstance().execute( new NewArtifactCommand( myMediator ) );
    }
  }

  @Override
  public void keyReleased(KeyEvent anE) {
    // TODO Auto-generated method stub

  }

  @Override
  public void keyTyped(KeyEvent anE) {
    // TODO Auto-generated method stub

  }

}
