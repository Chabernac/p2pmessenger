package chabernac.documentationtool;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

public class AssistListener implements KeyListener {
  private final JTextComponent myTextComponent;
  private final DocumentationToolMediator myMediator;

  public AssistListener(DocumentationToolMediator aMediator, JTextComponent aComponent) {
    myMediator = aMediator;
    myTextComponent = aComponent;
  }

  @Override
  public void keyPressed(KeyEvent anEvent) {
    if(anEvent.getKeyCode() == KeyEvent.VK_SPACE && anEvent.isControlDown()){
      myMediator.getAssistFrame().assist(myTextComponent.getDocument(), myTextComponent.getCaretPosition());
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
