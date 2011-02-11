/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.documentationtool.command;

import java.io.File;

import org.apache.log4j.Logger;

import chabernac.command.Command;
import chabernac.documentationtool.DocumentationToolMediator;

public class OpenArtifactCommand implements Command {
  private static Logger LOGGER = Logger.getLogger(OpenArtifactCommand.class);
  
  private final DocumentationToolMediator myMediator;
  private final File myFile;
  
  public OpenArtifactCommand( DocumentationToolMediator aMediator, File aFile ) {
    super();
    myMediator = aMediator;
    myFile = aFile;
  }

  @Override
  public void execute() {
    try {
      myMediator.getDocumentationFrame().openArtifact( myFile );
    } catch ( Exception e ) {
      LOGGER.error("Unable to open artifact", e);
    }
  }

}
