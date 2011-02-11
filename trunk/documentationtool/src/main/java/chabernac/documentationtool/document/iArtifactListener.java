/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.documentationtool.document;

import chabernac.documentationtool.document.Artifact.Attribute;

public interface iArtifactListener {
  public void attributeChanged(Artifact anArtifact, Attribute anAttribute);
}
