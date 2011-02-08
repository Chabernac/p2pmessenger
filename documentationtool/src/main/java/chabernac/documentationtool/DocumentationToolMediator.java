package chabernac.documentationtool;


public class DocumentationToolMediator {
  private DocumentationFrame myDocumentationFrame;
  private iDocumentAssistant myAssistFrame;
  public DocumentationFrame getDocumentationFrame() {
    return myDocumentationFrame;
  }
  public void setDocumentationFrame(DocumentationFrame anDocumentationFrame) {
    myDocumentationFrame = anDocumentationFrame;
  }
  public iDocumentAssistant getAssistFrame() {
    return myAssistFrame;
  }
  public void setAssistFrame(iDocumentAssistant anAssistFrame) {
    myAssistFrame = anAssistFrame;
  }
  
  
}
