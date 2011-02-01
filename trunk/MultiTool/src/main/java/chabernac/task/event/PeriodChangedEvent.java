/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.task.event;

import chabernac.event.Event;
import chabernac.task.Period;

public class PeriodChangedEvent extends Event {
  private final Period myPeriod;

  public PeriodChangedEvent( Period aPeriod ) {
    super( "Period changed" );
    myPeriod = aPeriod;
  }

  public Period getPeriod() {
    return myPeriod;
  }
}
