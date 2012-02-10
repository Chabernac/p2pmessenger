/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.command;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;

import chabernac.command.CommandSession.Mode;
import chabernac.util.concurrent.iRunnableListener;
import chabernac.util.concurrent.MonitorrableRunnable.Status;

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
  private CommandSession mySession;
  
  static{
    BasicConfigurator.resetConfiguration();
    BasicConfigurator.configure();
  }
  
  public void setUp(){
    mySession = CommandSession.getInstance();
    mySession.setMode( Mode.NORMAL );
    mySession.setNumberOfThreads(0);
  }
  
  public void testSession(){
    StringBuffer theBuffer = new StringBuffer();
    mySession.execute( new StringBufferAddCommand(theBuffer, "a") );
    assertEquals( "a", theBuffer.toString() );
    mySession.execute( new StringBufferAddCommand(theBuffer, "b") );
    assertEquals( "ab", theBuffer.toString() );
    mySession.undoNumberOfSteps( 1 );
    assertEquals( "a", theBuffer.toString() );
    mySession.redoNumberOfSteps( 1 );
    assertEquals( "ab", theBuffer.toString() );
    mySession.execute( new StringBufferAddCommand(theBuffer, "c") );
    assertEquals( "abc", theBuffer.toString() );
    mySession.undoNumberOfSteps( 2 );
    assertEquals( "a", theBuffer.toString() );
    mySession.redoNumberOfSteps( 1 );
    assertEquals( "ab", theBuffer.toString() );
    mySession.redoNumberOfSteps( 1 );
    assertEquals( "abc", theBuffer.toString() );
    mySession.redoNumberOfSteps( 2 );
    assertEquals( "abc", theBuffer.toString() );
    mySession.undoAll();
    assertEquals( "", theBuffer.toString() );
    mySession.redoAll();
    assertEquals( "abc", theBuffer.toString() );
    mySession.redoAll();
    assertEquals( "abc", theBuffer.toString() );
    mySession.execute( new StringBufferAddCommand(theBuffer, "d") );
    mySession.undoNumberOfSteps( 3 );
    assertEquals( "a", theBuffer.toString() );
    mySession.redoNumberOfSteps( 3 );
    assertEquals( "abcd", theBuffer.toString() );

    mySession.reset();
    mySession.undoNumberOfSteps( 2 );
    assertEquals( "abcd", theBuffer.toString() );
    mySession.redoNumberOfSteps( 2 );
    assertEquals( "abcd", theBuffer.toString() );
    mySession.execute( new StringBufferAddCommand(theBuffer, "e") );
    assertEquals( "abcde", theBuffer.toString() );
    mySession.execute( new StringBufferAddCommand(theBuffer, "f") );
    assertEquals( "abcdef", theBuffer.toString() );
    mySession.undoNumberOfSteps( 1 );
    assertEquals( "abcde", theBuffer.toString() );
    mySession.redoAll();
    assertEquals( "abcdef", theBuffer.toString() );
    mySession.undoAll();
    assertEquals( "abcd", theBuffer.toString() );
  }

  public void testCommandSessionInstance(){
    Map theContext = new HashMap();
    CommandSession mySession = CommandSession.getInstance();
    assertEquals( mySession, CommandSession.getInstance() );
    mySession = CommandSession.getInstance(theContext);
    assertEquals( mySession, CommandSession.getInstance(theContext) );
  }

  public void testCommandSessionMode(){
    StringBuffer theBuffer = new StringBuffer();
    CommandSession mySession = CommandSession.getInstance();
    mySession.execute( new StringBufferAddCommand(theBuffer, "a") );

    assertEquals( "a", theBuffer.toString());

    mySession.setMode( Mode.SKIP );

    mySession.execute( new StringBufferAddCommand(theBuffer, "b") );
    assertEquals( "a", theBuffer.toString());

    mySession.setMode( Mode.NORMAL);
    mySession.execute( new StringBufferAddCommand(theBuffer, "b") );
    assertEquals( "ab", theBuffer.toString());

    mySession.setMode( Mode.EXCEPTION);
    try{
      mySession.execute( new StringBufferAddCommand(theBuffer, "c") );

      fail( "should not get here because of exception" );
    }catch(CommandException e){

    }
  }

  public void testMultiThreadedCommandSession() throws InterruptedException{
    CommandSession mySession = CommandSession.getInstance(new HashMap());
    mySession.setNumberOfThreads( 2 );

    int theCount = 100;
    final CountDownLatch theLatch = new CountDownLatch( theCount );

    for(int i=0;i<theCount;i++){
      mySession.execute( new Command(){
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
    CommandSession mySession = CommandSession.getInstance(new HashMap());
    mySession.setNumberOfThreads( theThreads );

    int theCount = 100;
    final CountDownLatch theLatch = new CountDownLatch( theCount );
    int theBlocked = 2;

    for(int i=0;i<theBlocked;i++){
      mySession.execute( new Command() {
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
      mySession.execute( new Command(){
        @Override
        public void execute() {
          theLatch.countDown();
        }
      });
    }

    theLatch.await( 2, TimeUnit.SECONDS );
    assertEquals( 0, theLatch.getCount() );
  }
  
  public void testExecutionWithMonitor() throws InterruptedException{
   RunnableMonitor theMonitor = new RunnableMonitor();
   CommandSession mySession = CommandSession.getInstance();
   mySession.setMode( Mode.NORMAL );
   mySession.setNumberOfThreads( 1 );
   mySession.setRunnableListener( theMonitor );
   
   StringBuffer theBuffer = new StringBuffer();
   mySession.execute( new StringBufferAddCommand(theBuffer, "a") );
   Thread.sleep( 200 );
   assertEquals( "a", theBuffer.toString() );
   
   assertEquals( 1,theMonitor.getStatusMap().size());
   assertEquals( Status.END, theMonitor.getStatusMap().values().iterator().next() );

   mySession.execute( new PauseCommand( 1000 ) );
   Thread.sleep( 100 );
   assertEquals( 1, theMonitor.getStatusMap().size());
   assertEquals( Status.RUNNING, theMonitor.getStatusMap().values().toArray()[0]);
   Thread.sleep( 2000 );
   assertEquals( Status.END, theMonitor.getStatusMap().values().toArray()[0]);
  }
  
  private class RunnableMonitor implements iRunnableListener{
    private Map<Thread, Status> myStatusMap = new HashMap<Thread, Status>();

    @Override
    public void statusChanged( Status aStatus, String anExtraInfo ) {
      myStatusMap.put(Thread.currentThread(), aStatus);
    }
    
    public Map<Thread, Status> getStatusMap(){
      return myStatusMap;
    }
  }
}
