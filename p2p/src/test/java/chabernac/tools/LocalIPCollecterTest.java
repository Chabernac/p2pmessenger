/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.tools;

import junit.framework.TestCase;

public class LocalIPCollecterTest extends TestCase {
  public void testLocalIPCollecter() throws InterruptedException{
    LocalIPCollecter theCollector = new LocalIPCollecter(null, 1);
    try{

      theCollector.addIPListener( new LoggingIPListener() );
      theCollector.start();

    }finally{
      theCollector.stop();
    }
  }
}
