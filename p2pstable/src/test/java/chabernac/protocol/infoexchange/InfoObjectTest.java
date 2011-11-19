/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.infoexchange;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.TestCase;
import chabernac.protocol.infoexchange.InfoObject;

/**
 *
 * <br><br>
 * <u><i>Version History</i></u>
 * <pre>
 * v2010.10.0 7-jul-2010 - DGCH804 - initial release
 *
 * </pre>
 *
 * @version v2010.10.0      7-jul-2010
 * @author <a href="mailto:guy.chauliac@axa.be"> Guy Chauliac</a>
 */
public class InfoObjectTest extends TestCase {
  public void testInfoObject(){
    InfoObject theInfoObject = new InfoObject();

    ObserverCounter theObserver = new ObserverCounter();
    theInfoObject.addObserver( theObserver );
    
    theInfoObject.put( "a", "b" );
    theInfoObject.put( "c", "d" );
    
    assertEquals( 2, theObserver.getCounter() );
  }
  
  private class ObserverCounter implements Observer{
    private AtomicLong myCounter = new AtomicLong();

    @Override
    public void update( Observable anO, Object anArg ) {
      myCounter.incrementAndGet();
    }
    
    public long getCounter(){
      return myCounter.get();
    }
  }
}
