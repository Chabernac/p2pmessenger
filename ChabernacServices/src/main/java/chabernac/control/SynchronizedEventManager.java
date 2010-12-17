/*
 * Created on 13-jul-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package chabernac.control;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class SynchronizedEventManager implements Runnable{
  private static Logger LOGGER = Logger.getLogger(SynchronizedEventManager.class);
//  private static final int PROCESSORS = Runtime.getRuntime().availableProcessors();
  private static final int PROCESSORS = 1;

  private List<iSynchronizedEvent> myList = null;
  private long myCycleTime = 0;
  private TimeUnit myTimeUnit = null;
  private double myFPS;
  
  private boolean isRecording = false;
  private Recording myRecording = null;
  
  private Recording myPlayRecording = null;
  
  private iRecordingRestorer myRestorer = null;

  private ScheduledExecutorService myService = Executors.newScheduledThreadPool( PROCESSORS );

  private boolean stop = false;
  private int myCounter = 0;

  public SynchronizedEventManager(int aFPS){
    this((long)(1000D / (double)aFPS), TimeUnit.MILLISECONDS);
  }

  public SynchronizedEventManager(long aCycleTime, TimeUnit aTimeUnit){
    myList =  new ArrayList<iSynchronizedEvent>() ;
    myCycleTime = aCycleTime;
    myTimeUnit = aTimeUnit;
  }

  public void addSyncronizedEvent(iSynchronizedEvent anEvent){
    myList.add(anEvent);
  }

  public void removeSyncronizedEvent(iSynchronizedEvent anEvent){
    myList.remove(anEvent);
  }

  public void startManager(){
    stop = false;
    for(int i=0;i<PROCESSORS;i++){
      myService.scheduleAtFixedRate( this, i * myCycleTime, PROCESSORS * myCycleTime, myTimeUnit ); 
    }
  }

  public void stopManager(){
    stop = true;
    myService.shutdownNow();
  }

  public boolean isRunning(){
    return !myService.isShutdown();
  }

  public double getFPS() {
    return myFPS;
  }

  public void setFPS(double anFps) {
    myFPS = anFps;
  }

  public void run(){
    try{
      myCounter++;
      
      for(iSynchronizedEvent theEvent : new ArrayList<iSynchronizedEvent>(myList) ){
        boolean isExecuted = theEvent.executeEvent(myCounter); 
        if(isRecording && isExecuted){
          myRecording.add(myCounter, theEvent);
        }
      }
      
      if(myPlayRecording != null){
        myPlayRecording.play( myCounter );
        if(myPlayRecording.size() == 0){
          myPlayRecording = null;
        }
      }
      
    }catch(Throwable e){
      LOGGER.error( "Error occured while cycling", e );
    }
  }
  
  public void playRecording(Recording aRecording){
    myPlayRecording = aRecording;
  }

  public boolean isRecording() {
    return isRecording;
  }

  public void setRecording( boolean aRecording ) {
    isRecording = aRecording;
    if(myRecording == null){
      newRecording();
    }
  }
  
  public void newRecording(){
    myRecording = new Recording( myCounter );
  }

  public void saveRecording(OutputStream anOutputStream) throws IOException{
    ObjectOutputStream theOut = null;
    try{
      theOut = new ObjectOutputStream(anOutputStream);
      theOut.writeObject( myRecording );
    } finally {
      if(theOut != null){
        theOut.flush();
        theOut.close();
      }
    }
  }
  
  public void loadRecording(InputStream anInputStream) throws Exception{
    ObjectInputStream theInput = null;
    try{
      theInput = new ObjectInputStream(anInputStream);
      Recording theRecording = (Recording)theInput.readObject();
      if(myRestorer != null) myRestorer.restoreRecording( theRecording );
      theRecording.setOffsset( myCounter );
      playRecording( theRecording );
    } finally {
      if(theInput != null){
        theInput.close();
      }
    }
  }

  public iRecordingRestorer getRestorer() {
    return myRestorer;
  }

  public void setRestorer( iRecordingRestorer aRestorer ) {
    myRestorer = aRestorer;
  }
}
