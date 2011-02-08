package chabernac.documentationtool;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;



public class AssistFrame implements iDocumentAssistant{

  @Override
  public void assist(Document aDocument, int aCursorPosition) {
    try {
      aDocument.insertString(aCursorPosition, "[test link]", null );
    } catch (BadLocationException e) {
      e.printStackTrace();
    }
  }

}
