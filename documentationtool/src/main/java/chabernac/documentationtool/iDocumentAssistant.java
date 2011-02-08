package chabernac.documentationtool;

import javax.swing.text.Document;


public interface iDocumentAssistant {
  /**
   * the implementation must show a list of available documents
   * after the user has chosen a document a link must be inserted in the document
   * at the cursor position
   * @param aDocument
   */
  public void assist(Document aDocument, int aCusorPosition);
}
