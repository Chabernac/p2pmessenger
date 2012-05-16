/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.tools;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;

public class LocalIPCollecterTest extends TestCase {
//  static{
//    BasicConfigurator.resetConfiguration();
//    BasicConfigurator.configure();
//  }
  
  public void testLocalIPCollecter() throws InterruptedException{
    LocalIPCollecter theCollector = new LocalIPCollecter(null, 1);
    try{

      theCollector.addIPListener( new LoggingIPListener() );
      theCollector.start();

//      Thread.sleep(500000);
    }finally{
      theCollector.stop();
    }
  }
}
