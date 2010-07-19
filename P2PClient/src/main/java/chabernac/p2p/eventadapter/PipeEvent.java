/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2p.eventadapter;

import chabernac.events.Event;
import chabernac.protocol.pipe.Pipe;

public class PipeEvent extends Event {
  private static final long serialVersionUID = 8867084780231288678L;
  private final Pipe myPipe;

  public PipeEvent ( Pipe anPipe ) {
    myPipe = anPipe;
  }

  public Pipe getPipe() {
    return myPipe;
  }
}
