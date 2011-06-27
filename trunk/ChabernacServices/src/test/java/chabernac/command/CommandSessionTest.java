/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.command;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;

import junit.framework.TestCase;
import chabernac.command.CommandSession.Mode;

/**
 *
 * <br><br>
 * <u><i>Version History</i></u>
 * <pre>
 * v2010.10.0 15-jan-2010 - DGCH804 - initial release
 *
 * </pre>
 *
 * @version v2010.10.0      15-jan-2010
 * @author <a href="mailto:guy.chauliac@axa.be"> Guy Chauliac </a>
 */

public class CommandSessionTest extends TestCase {
  static{
    BasicConfigurator.resetConfiguration();
    BasicConfigurator.configure();
  }
  
  public void testSession(){
    StringBuffer theBuffer = new StringBuffer();
    CommandSession theSession = CommandSession.getInstance();
    theSession.execute( new StringBufferAddCommand(theBuffer, "a") );
    assertEquals( "a", theBuffer.toString() );
    theSession.execute( new StringBufferAddCommand(theBuffer, "b") );
    assertEquals( "ab", theBuffer.toString() );
    theSession.undoNumberOfSteps( 1 );
    assertEquals( "a", theBuffer.toString() );
    theSession.redoNumberOfSteps( 1 );
    assertEquals( "ab", theBuffer.toString() );
    theSession.execute( new StringBufferAddCommand(theBuffer, "c") );
    assertEquals( "abc", theBuffer.toString() );
    theSession.undoNumberOfSteps( 2 );
    assertEquals( "a", theBuffer.toString() );
    theSession.redoNumberOfSteps( 1 );
    assertEquals( "ab", theBuffer.toString() );
    theSession.redoNumberOfSteps( 1 );
    assertEquals( "abc", theBuffer.toString() );
    theSession.redoNumberOfSteps( 2 );
    assertEquals( "abc", theBuffer.toString() );
    theSession.undoAll();
    assertEquals( "", theBuffer.toString() );
    theSession.redoAll();
    assertEquals( "abc", theBuffer.toString() );
    theSession.redoAll();
    assertEquals( "abc", theBuffer.toString() );
    theSession.execute( new StringBufferAddCommand(theBuffer, "d") );
    theSession.undoNumberOfSteps( 3 );
    assertEquals( "a", theBuffer.toString() );
    theSession.redoNumberOfSteps( 3 );
    assertEquals( "abcd", theBuffer.toString() );

    theSession.reset();
    theSession.undoNumberOfSteps( 2 );
    assertEquals( "abcd", theBuffer.toString() );
    theSession.redoNumberOfSteps( 2 );
    assertEquals( "abcd", theBuffer.toString() );
    theSession.execute( new StringBufferAddCommand(theBuffer, "e") );
    assertEquals( "abcde", theBuffer.toString() );
    theSession.execute( new StringBufferAddCommand(theBuffer, "f") );
    assertEquals( "abcdef", theBuffer.toString() );
    theSession.undoNumberOfSteps( 1 );
    assertEquals( "abcde", theBuffer.toString() );
    theSession.redoAll();
    assertEquals( "abcdef", theBuffer.toString() );
    theSession.undoAll();
    assertEquals( "abcd", theBuffer.toString() );
  }

  public void testCommandSessionInstance(){
    Map theContext = new HashMap();
    CommandSession theSession = CommandSession.getInstance();
    assertEquals( theSession, CommandSession.getInstance() );
    theSession = CommandSession.getInstance(theContext);
    assertEquals( theSession, CommandSession.getInstance(theContext) );
  }

  public void testCommandSessionMode(){
    StringBuffer theBuffer = new StringBuffer();
    CommandSession theSession = CommandSession.getInstance();
    theSession.execute( new StringBufferAddCommand(theBuffer, "a") );

    assertEquals( "a", theBuffer.toString());

    theSession.setMode( Mode.SKIP );

    theSession.execute( new StringBufferAddCommand(theBuffer, "b") );
    assertEquals( "a", theBuffer.toString());

    theSession.setMode( Mode.NORMAL);
    theSession.execute( new StringBufferAddCommand(theBuffer, "b") );
    assertEquals( "ab", theBuffer.toString());

    theSession.setMode( Mode.EXCEPTION);
    try{
      theSession.execute( new StringBufferAddCommand(theBuffer, "c") );

      fail( "should not get here because of exception" );
    }catch(CommandException e){

    }
  }

  public void testMultiThreadedCommandSession() throws InterruptedException{
    CommandSession theSession = CommandSession.getInstance(new HashMap());
    theSession.setNumberOfThreads( 2 );

    int theCount = 100;
    final CountDownLatch theLatch = new CountDownLatch( theCount );

    for(int i=0;i<theCount;i++){
      theSession.execute( new Command(){
        @Override
        public void execute() {
          theLatch.countDown();
        }
      });
    }

    theLatch.await( 2, TimeUnit.SECONDS );
    assertEquals( 0, theLatch.getCount() );
  }

  public void testBlockedCommandMultiThreadedCommandSession() throws InterruptedException{
    int theThreads = 3;
    CommandSession theSession = CommandSession.getInstance(new HashMap());
    theSession.setNumberOfThreads( theThreads );

    int theCount = 100;
    final CountDownLatch theLatch = new CountDownLatch( theCount );
    int theBlocked = 2;

    for(int i=0;i<theBlocked;i++){
      theSession.execute( new Command() {
        @Override
        public void execute() {
          try {
            Thread.sleep( 60000 );
          } catch ( InterruptedException e ) {
          }
        }
      });
    }

    //2 threads are blocked but the other one should still process the commands
    
    for(int i=0;i<theCount;i++){
      theSession.execute( new Command(){
        @Override
        public void execute() {
          theLatch.countDown();
        }
      });
    }

    theLatch.await( 2, TimeUnit.SECONDS );
    assertEquals( 0, theLatch.getCount() );
  }
}
