/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pclient.gui.action;


import org.apache.log4j.Logger;

import chabernac.command.Command;
import chabernac.protocol.facade.P2PFacade;
import chabernac.protocol.facade.P2PFacadeException;
import chabernac.protocol.userinfo.UserInfo.Status;

public class ChangeStatusCommand implements Command {
  private static final Logger LOGGER = Logger.getLogger(ChangeStatusCommand.class);
  
  private final Status myStatus;
  private final P2PFacade myFacade;

  public ChangeStatusCommand ( P2PFacade aFacade, Status aStatus ){
    myStatus = aStatus;
    myFacade = aFacade;
  }

  @Override
  public void execute() {
    try {
      myFacade.getPersonalInfo().setStatus( myStatus );
    } catch ( P2PFacadeException e ) {
      LOGGER.error("Could not change status", e);
    }
  }
}
