/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import java.util.Set;

public interface iTransferContainer {
  public void addTransferListener(iTransferListener aListener);
  public void removeTransferListener(iTransferListener aListener);
  public Set< AbstractTransferState > getTransferStates();
  public AbstractTransferState getTransferState(String aTransferId);
}
