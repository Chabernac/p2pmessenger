/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.documentationtool;

import java.awt.Color;
import java.io.File;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.log4j.Logger;

import chabernac.documentationtool.document.Artifact;
import chabernac.documentationtool.document.DocumentationBase;
import chabernac.documentationtool.document.DocumentationBaseException;

public class DocumentationBaseTextArea extends JTextArea {
  private static final Logger LOGGER = Logger.getLogger( DocumentationArea.class );
  private final Artifact myArtifact;

  public DocumentationBaseTextArea( Artifact aArtifact ) {
    super();
    myArtifact = aArtifact;
    addListeners();
    setBorder( BorderFactory.createLineBorder(Color.black) );
    setRows( 3 );
    loadArtifact();
  }
  
  private void loadArtifact(){
    String theText = "";
    for(Iterator<DocumentationBase> i = myArtifact.getDocumentationBase().iterator(); i.hasNext();){
       theText += i.next().toString() + "\r\n";
    }
    setText( theText );
  }
  
  private void addListeners(){
    getDocument().addDocumentListener( new MyDocumentListener() );
  }
  
  private void setDocumentBase(){
    myArtifact.clearDocumantationBase();
    String[] theText = getText().split( "\r\n" );
    for(String theBase : theText){
      try{
        myArtifact.addDocumentationBase( createDocumentationBase(theBase) );
      }catch(Exception e){
//        LOGGER.error("Wrong base", e);
      }
    }
  }
  
  private DocumentationBase createDocumentationBase(String aBase) throws DocumentationBaseException{
    String[] theParts = aBase.split( "=" );
    if(theParts.length != 2) informWrongBase(aBase, "Base must be formatted as: 'name=location'");
    String theName = theParts[0];
    File theFile = new File(theParts[1]);
    if(!theFile.exists()) informWrongBase(aBase, "The base location does not exist");
    if(!theFile.isDirectory()) informWrongBase(aBase, "The base location must be a directory");
    
    return new DocumentationBase( theName, theFile);
  }
  
  private void informWrongBase( String aBase, String aString ) throws DocumentationBaseException{
    throw new DocumentationBaseException("Wrong base '" + aBase + "': " + aString);
  }

  private class MyDocumentListener implements DocumentListener{

    @Override
    public void changedUpdate( DocumentEvent aE ) {
       setDocumentBase();
    }

    @Override
    public void insertUpdate( DocumentEvent aE ) {
      setDocumentBase();
    }

    @Override
    public void removeUpdate( DocumentEvent aE ) {
      setDocumentBase();
    }
  }
}
