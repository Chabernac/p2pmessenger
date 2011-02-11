/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.documentationtool;

import java.util.ArrayList;

import javax.swing.AbstractListModel;

import chabernac.documentationtool.document.Artifact;

public class ArtifactBaseListModel extends AbstractListModel {
  private static final long serialVersionUID = 627328817016332904L;
  private final Artifact myArtifact;
  
  public ArtifactBaseListModel( Artifact aArtifact ) {
    super();
    myArtifact = aArtifact;
  }

  @Override
  public Object getElementAt( int aIndex ) {
    return new ArrayList(myArtifact.getDocumentationBase()).get(aIndex);
  }

  @Override
  public int getSize() {
    return myArtifact.getDocumentationBase().size();
  }
  
  public void fireChanged(){
    fireContentsChanged( this, 0, myArtifact.getDocumentationBase().size() );
  }
}
