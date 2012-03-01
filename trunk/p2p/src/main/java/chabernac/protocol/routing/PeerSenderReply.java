/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import chabernac.tools.PropertyMap;

public class PeerSenderReply {
  private final String myReply;
  private final PropertyMap myProperties;
  
  public PeerSenderReply( String aReply, PropertyMap aProp ) {
    super();
    myReply = aReply;
    myProperties = aProp;
  }

  public String getReply() {
    return myReply;
  }

  public PropertyMap getProperties() {
    return myProperties;
  }
}
