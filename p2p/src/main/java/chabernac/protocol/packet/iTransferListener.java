/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import chabernac.protocol.packet.AbstractTransferState.Direction;

public interface iTransferListener {
  public void newTransfer(AbstractTransferState aTransfer, Direction aDirection);
  public void transferRemoved(AbstractTransferState aTransfer);
}
