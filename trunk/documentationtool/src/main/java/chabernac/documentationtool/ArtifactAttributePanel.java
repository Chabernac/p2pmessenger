/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.documentationtool;

import javax.swing.JTextArea;

import chabernac.documentationtool.document.Artifact;
import chabernac.documentationtool.document.iArtifactListener;
import chabernac.documentationtool.document.Artifact.Attribute;

public class ArtifactAttributePanel extends JTextArea implements iArtifactListener{
  private final Artifact myArtifact;

  public ArtifactAttributePanel( Artifact aArtifact ) {
    super();
    myArtifact = aArtifact;
    aArtifact.addArtifactListener( this );
    loadAttributes();
  }

  @Override
  public void attributeChanged( Artifact anArtifact, Attribute anAttribute ) {
    loadAttributes();
  }
  
  private void loadAttributes(){
    String theText = "";
    for(Attribute theAttribute : Artifact.Attribute.values()){
      if(myArtifact.getAttribute( theAttribute ) != null){
        theText += theAttribute.name() + "=" +  myArtifact.getAttribute( theAttribute ) + "\r\n";
      }
    }
    setText( theText );
  }
  

}
