/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.documentationtool.command;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class DocJFilter extends FileFilter {

  @Override
  public boolean accept( File aF ) {
    return aF.getName().endsWith( ".docj" );
  }

  @Override
  public String getDescription() {
    return "docj filter";
  }

}
