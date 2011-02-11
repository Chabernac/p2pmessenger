package chabernac.documentationtool;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JTextPane;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;

import chabernac.documentationtool.document.Artifact;

public class DocumentationArea extends JTextPane {
 
  private static final long serialVersionUID = 7496385040153987784L;
  private final DocumentationToolMediator myMediator;
  private final Artifact<Document> myArtifact;
  
  public DocumentationArea(DocumentationToolMediator anMediator, Artifact<Document> anArtifact) {
    super(anArtifact.getContent() == null ? (StyledDocument)new JTextPane().getDocument() : (StyledDocument)anArtifact.getContent());
    myMediator = anMediator;
    myArtifact = anArtifact;
    myArtifact.setContent( getDocument() );
    addListeners();
    setBorder( BorderFactory.createLineBorder(Color.black) );
  }

  
  private void addListeners(){
    addKeyListener(new AssistListener(myMediator, this));
    LinkListener theLinkListener = new LinkListener(myMediator, this);
    addMouseListener(theLinkListener);
    addMouseMotionListener(theLinkListener);
  }
  
  public Artifact<Document> getArtifact(){
    return myArtifact;
  }

}

