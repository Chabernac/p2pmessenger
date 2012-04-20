/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

public interface iStateChangeListener {
  public void stateChanged(String aTransferId, AbstractTransferState.State anOldState, AbstractTransferState.State aNewState);
}
