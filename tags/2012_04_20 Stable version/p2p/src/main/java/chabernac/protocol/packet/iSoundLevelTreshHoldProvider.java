/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

public interface iSoundLevelTreshHoldProvider {
  public double getThreshHold();
  public void currentRecordingSoundLevel(double aSoundLevel);
  public void currentPlayingSoundLevel(double aSoundLevel);
}
