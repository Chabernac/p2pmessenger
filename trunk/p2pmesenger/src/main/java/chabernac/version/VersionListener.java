/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.version;

import java.util.Map;

public interface VersionListener {
  public void versionChanged(String aPeerId, Version aVersion, Map<String, Version> anAllVersions);
}
