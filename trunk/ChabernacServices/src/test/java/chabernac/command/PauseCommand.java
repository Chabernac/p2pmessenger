/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.command;

public class PauseCommand implements Command {
  private final long myTimeout;
  
  public PauseCommand( long aTimeout ) {
    super();
    myTimeout = aTimeout;
  }

  @Override
  public void execute() {
    try {
      Thread.sleep( myTimeout );
    } catch ( InterruptedException e ) {
    }
  }

}
