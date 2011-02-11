package chabernac.documentationtool.launch;

import org.apache.log4j.BasicConfigurator;

import chabernac.documentationtool.AssistFrame;
import chabernac.documentationtool.DocumentationFrame;
import chabernac.documentationtool.DocumentationToolMediator;
import chabernac.documentationtool.iDocumentAssistant;

public class ApplicationLauncher {

  /**
   * @param args
   */
  public static void main(String[] args) {
    BasicConfigurator.configure();
    
    iDocumentAssistant theAssistent = new AssistFrame();
    
    DocumentationToolMediator theMediator = new DocumentationToolMediator();
    DocumentationFrame theFrame = new DocumentationFrame(theMediator);
        
    theMediator.setDocumentationFrame(theFrame);
    theMediator.setAssistFrame(theAssistent);
    
    theFrame.setVisible(true);
  }

}
