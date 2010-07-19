/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2p.eventadapter;

import java.util.Map;

import chabernac.events.Event;
import chabernac.protocol.infoexchange.InfoObject;

public class InfoChangedEvent extends Event {
  private static final long serialVersionUID = 8005794148952619476L;
  
  private final String aPeerId;
  private final Map< String, InfoObject > aInfoMap;
  
  public InfoChangedEvent ( String anPeerId , Map< String, InfoObject > anInfoMap ) {
    super();
    aPeerId = anPeerId;
    aInfoMap = anInfoMap;
  }

  public String getAPeerId() {
    return aPeerId;
  }

  public Map< String, InfoObject > getAInfoMap() {
    return aInfoMap;
  }
}
