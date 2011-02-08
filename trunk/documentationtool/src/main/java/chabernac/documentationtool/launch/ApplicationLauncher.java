package chabernac.documentationtool.launch;

import chabernac.documentationtool.AssistFrame;
import chabernac.documentationtool.DocumentationFrame;
import chabernac.documentationtool.DocumentationToolMediator;
import chabernac.documentationtool.iDocumentAssistant;

public class ApplicationLauncher {

  /**
   * @param args
   */
  public static void main(String[] args) {
    iDocumentAssistant theAssistent = new AssistFrame();
    
    DocumentationToolMediator theMediator = new DocumentationToolMediator();
    DocumentationFrame theFrame = new DocumentationFrame(theMediator);
    
    theMediator.setDocumentationFrame(theFrame);
    theMediator.setAssistFrame(theAssistent);
    
    theFrame.setSize(800, 600);
    theFrame.setVisible(true);
  }

}
