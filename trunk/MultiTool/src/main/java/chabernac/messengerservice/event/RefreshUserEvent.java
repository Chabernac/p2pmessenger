/**
 * Copyright (c) 2009 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.messengerservice.event;

import chabernac.event.Event;

public class RefreshUserEvent extends Event {

  public RefreshUserEvent ( ) {
    super( "Refresh user list");
  }

}
