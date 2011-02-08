package chabernac.documentationtool;

import javax.swing.JTextPane;

public class DocumentationArea extends JTextPane {
 
  private static final long serialVersionUID = 7496385040153987784L;
  private final DocumentationToolMediator myMediator;
  
  public DocumentationArea(DocumentationToolMediator anMediator) {
    myMediator = anMediator;
    addListeners();
  }

  
  private void addListeners(){
    addKeyListener(new AssistListener(myMediator, this));
    LinkListener theLinkListener = new LinkListener(myMediator, this);
    addMouseListener(theLinkListener);
    addMouseMotionListener(theLinkListener);
    
  }

}

