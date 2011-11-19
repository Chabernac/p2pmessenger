/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import chabernac.p2p.debug.WhoIsFrame;
import junit.framework.TestCase;

public class WhoIsFrameTest extends TestCase {
  public void testWhoIsFrame(){
    WhoIsFrame theFrame = new WhoIsFrame();
    theFrame.setVisible( true );
  }
}
