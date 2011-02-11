/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.documentationtool;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.text.Document;

import chabernac.documentationtool.document.Artifact;

public class ArtifactDocumenationBasePanelTestFrame {
  public static void main(String args[]){
    Artifact<Document> theArtifact = Artifact.getDocumentInstance();
    DocumentationBasePanel thePanel = new DocumentationBasePanel( theArtifact );
    JFrame theFrame = new JFrame();
    theFrame.getContentPane().setLayout( new BorderLayout() );
    theFrame.getContentPane().add(thePanel, BorderLayout.CENTER);
    theFrame.setSize( 300,300 );
    theFrame.setVisible( true );
  }
}
