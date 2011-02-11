/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.documentationtool.command;

import org.apache.log4j.Logger;

import chabernac.command.Command;
import chabernac.documentationtool.DocumentationToolMediator;
import chabernac.documentationtool.document.Artifact;

public class NewArtifactCommand implements Command {
  private static Logger LOGGER = Logger.getLogger(NewArtifactCommand.class);
  
  private final DocumentationToolMediator myMediator;

  public NewArtifactCommand( DocumentationToolMediator aMediator ) {
    super();
    myMediator = aMediator;
  }

  @Override
  public void execute() {
    myMediator.getDocumentationFrame().openArtifact( Artifact.getDocumentInstance() );
   
  }

}
