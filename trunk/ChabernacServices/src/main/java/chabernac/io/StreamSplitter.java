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
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import chabernac.utils.NamedRunnable;

public class StreamSplitter {
  private final Logger LOGGER = Logger.getLogger(StreamSplitter.class);
  public static final String IN = "I:";
  public static final String OUT = "O:";

  private final BufferedReader myInputStream;
  private final PrintWriter myOutputStream;
  private final iInputOutputHandler myInputOutputHandler;

  private final ArrayBlockingQueue<String> myOutputQueue = new ArrayBlockingQueue<String>(128);

  private List<iStreamListener> myStreamListeners = new ArrayList<iStreamListener>();

  private String myId;
  private boolean isClosed = false;

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
    for(iStreamListener theListener : new ArrayList<iStreamListener>(myStreamListeners)){
      theListener.streamClosed();
    }
  }

  public void close(){
    myOutputStream.close();
    try {
      myInputStream.close();
    } catch (IOException e) {
    }
    isClosed = true;
  }
  
  public boolean isClosed(){
    return isClosed;
  }

  public String send(String anInput) throws InterruptedException{
//    LOGGER.debug(myId + ":SENDING INPUT: '" + anInput + "'");
    sendWithoutReply(IN + anInput);
    String theReply = myOutputQueue.poll(5, TimeUnit.SECONDS);
    if(theReply == null) throw new InterruptedException("No reply received within 5 seconds");
//    LOGGER.debug(myId + ":RETURNING OUTPUT: '" + theReply + "'");
    return theReply;
  }

  public String readLine() throws IOException{
    return myInputStream.readLine();
  }

  public void startSplitting(ExecutorService anExecutorService){
    anExecutorService.execute(new InputHandler());
  }

  public void sendWithoutReply(String anInput){
    myOutputStream.println(anInput);
    myOutputStream.flush();
  }

  public String getId() {
    return myId;
  }

  public void setId( String aId ) {
    myId = aId;
  }

  public void handleInput(String anInput){
    String theOutput = myInputOutputHandler.handle(myId, anInput);
    sendWithoutReply(OUT + theOutput);
  }

  private class InputHandler extends NamedRunnable{
    public void doRun(){
      String theLine = null;
      try{
//        System.out.println("Inputhandler running for server with remote id '" + myId + "'");
        while((theLine = myInputStream.readLine()) != null){
          if(theLine.startsWith(IN)){
//            LOGGER.debug(myId +  ":INPUT RECEIVED: '" + theLine + "'");
            String theInput = theLine.substring(IN.length());
            handleInput(theInput);
          } else {
//            LOGGER.debug(myId + ":OUTPUT RECEIVED: '" + theLine + "'");
            String theResponse = theLine;
            if(theResponse.startsWith(OUT)) theResponse = theResponse.substring(OUT.length());
            myOutputQueue.put(theResponse);
          }
        }
//        System.out.println("Line null");
      }catch(Exception e){
        LOGGER.error("Error occured while reading stream", e);
      } finally {
        notifyListeners();
      }
    }
  }
}
