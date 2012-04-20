/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.packet;

import org.apache.log4j.Logger;

public class BasicSoundLevelThreshHoldProvider implements iSoundLevelTreshHoldProvider{
  private final static Logger LOGGER = Logger.getLogger(BasicSoundLevelThreshHoldProvider.class);

  @Override
  public double getThreshHold() {
    return 1.5;
  }

  @Override
  public void currentRecordingSoundLevel( double aSoundLevel ) {
    LOGGER.debug("Current sound level '" + aSoundLevel + "'");
  }

  @Override
  public void currentPlayingSoundLevel( double aSoundLevel ) {
    // TODO Auto-generated method stub
    
  }
  
}
