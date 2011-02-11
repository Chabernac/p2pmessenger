/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.documentationtool.command;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Set;

import javax.swing.JFileChooser;

import org.apache.log4j.Logger;

import chabernac.command.Command;
import chabernac.documentationtool.DocumentationToolMediator;
import chabernac.documentationtool.document.Artifact;
import chabernac.documentationtool.document.DocumentationBase;
import chabernac.documentationtool.document.Artifact.Attribute;

public class SaveCommand implements Command {
  private static Logger LOGGER = Logger.getLogger(SaveCommand.class);
  
  private final DocumentationToolMediator myMediator;

  public SaveCommand( DocumentationToolMediator aMediator ) {
    super();
    myMediator = aMediator;
  }

  @Override
  public void execute() {
    try{
    Artifact theArtifact = myMediator.getDocumentationFrame().getCurrentArtifact();
    File theFile = null;
    if(theArtifact.getAttribute( Attribute.Location ) == null){
      Set<DocumentationBase> theDocumentationBase = theArtifact.getDocumentationBase();
      JFileChooser theChooser = new JFileChooser( theDocumentationBase.iterator().next().getLocation() );
      theChooser.setFileFilter( new DocJFilter() );
      theChooser.showSaveDialog( null );
      theFile = theChooser.getSelectedFile();
    } else {
      theFile= new File(theArtifact.getAttribute( Attribute.Location ));
    }
    theArtifact.setAttribute( Attribute.Name, theFile.getName() );
    theArtifact.setAttribute( Attribute.Location, theFile.getAbsolutePath() );
    theArtifact.save( new FileOutputStream( theFile ));
    myMediator.getDocumentationFrame().setTabName(theArtifact);
    }catch(Exception e){
      LOGGER.error("Unable to save artifact", e);
    }
  }

}
