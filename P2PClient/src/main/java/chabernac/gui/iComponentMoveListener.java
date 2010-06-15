/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui;

import java.awt.Component;

public interface iComponentMoveListener {
  public void componentDropped(Component aTarget, Component aSource, boolean isAfter);
  public void drawSeperator( Component anComponent, boolean anInsertComponentAfter );
  public void removeSeparator();
}
