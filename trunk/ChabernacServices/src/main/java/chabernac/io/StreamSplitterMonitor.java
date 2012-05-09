package chabernac.io;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StreamSplitterMonitor {
  private final StreamSplitter myStreamSplitter;
  private final Set<iStreamSplitterMonitorListener> myListeners = new HashSet<iStreamSplitterMonitorListener>();
  private boolean isActive = false;
  private long myLastActiveTime = -1;

  public StreamSplitterMonitor(StreamSplitter aStreamSplitter) {
    super();
    myStreamSplitter = aStreamSplitter;
    final ScheduledExecutorService theService = Executors.newScheduledThreadPool(1);
    theService.scheduleAtFixedRate(new StreamSplitterTester(), 0, 5, TimeUnit.SECONDS);
    aStreamSplitter.addStreamListener(new StreamSplitterListenerAdapter() {
      @Override
      public void streamClosed() {
        theService.shutdownNow();
      }
    });
  }
  
  private void notifyListeners(boolean isActive){
    myLastActiveTime = System.currentTimeMillis();
    this.isActive = isActive;
    for(iStreamSplitterMonitorListener theListener : myListeners){
      theListener.streamActive(isActive);
    }
  }
  
  public boolean isActive() {
    return isActive;
  }
  
  public long getLastActiveTime(){
    return myLastActiveTime;
  }

  public void addStreamSplitterMonitorListener(iStreamSplitterMonitorListener aListener){
    myListeners.add(aListener);
  }
  
  public void removeStreamSplitterMonitorListener(iStreamSplitterMonitorListener aListener){
    myListeners.remove(aListener);
  }

  public class StreamSplitterTester implements Runnable {
    @Override
    public void run() {
      try {
        notifyListeners("1".equals(myStreamSplitter.send("ECO1")));
      } catch (InterruptedException e) {
        notifyListeners(false);
      }
    }
  }

  public StreamSplitter getStreamSplitter() {
    return myStreamSplitter;
  }
}
