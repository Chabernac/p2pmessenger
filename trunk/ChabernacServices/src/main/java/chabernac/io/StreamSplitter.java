package chabernac.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

public class StreamSplitter {
  private final Logger LOGGER = Logger.getLogger(StreamSplitter.class);
  private final String IN = "I:";
  private final String OUT = "O:";
  
  private final BufferedReader myInputStream;
  private final PrintWriter myOutputStream;
  private final iInputOutputHandler myInputOutputHandler;
  
  private final ArrayBlockingQueue<String> myOutputQueue = new ArrayBlockingQueue<String>(128);
  
  private ExecutorService myExecutorService = null;
  
  private List<iStreamListener> myStreamListeners = new ArrayList<iStreamListener>();
  public StreamSplitter(InputStream aInputStream, OutputStream aOutputStream, iInputOutputHandler aInputOutputHandler) {
    super();
    myInputOutputHandler = aInputOutputHandler;
    myInputStream = new BufferedReader(new InputStreamReader(aInputStream));
    myOutputStream = new PrintWriter(new OutputStreamWriter(aOutputStream));
  }
  
  public void addStreamListener(iStreamListener aListener){
    myStreamListeners.add(aListener);
  }
  
  private void notifyListeners(){
    for(iStreamListener theListener : myStreamListeners){
      theListener.streamClosed();
    }
  }
  
  public void close(){
    try {
      myInputStream.close();
    } catch (IOException e) {
    }
    myOutputStream.close();
    myExecutorService.shutdownNow();
  }

  public String send(String anInput) throws InterruptedException{
    sendWithoutReply(IN + anInput);
    return myOutputQueue.take();
  }
  
  public String readLine() throws IOException{
    return myInputStream.readLine();
  }
  
  public void startSplitting(){
    myExecutorService = Executors.newSingleThreadExecutor();
    myExecutorService.execute(new InputHandler());
  }
  
  public void stopSplitting(){
    if(myExecutorService != null) myExecutorService.shutdownNow();
  }
  
  public void sendWithoutReply(String anInput){
    myOutputStream.println(anInput);
    myOutputStream.flush();
  }
  
  private class InputHandler implements Runnable{
    public void run(){
      String theLine = null;
      try{
      while((theLine = myInputStream.readLine()) != null){
        if(theLine.startsWith(IN)){
          String theInput = theLine.substring(IN.length());
          String theOutput = myInputOutputHandler.handle(theInput);
          sendWithoutReply(OUT + theOutput);
        } else {
          String theResponse = theLine;
          if(theResponse.startsWith(OUT)) theResponse = theResponse.substring(OUT.length());
          myOutputQueue.put(theResponse);
        }
      }
      }catch(Exception e){
        LOGGER.error("Error occured while reading stream", e);
      } finally {
        notifyListeners();
      }
    }
  }
}
