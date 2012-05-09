package chabernac.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

  private Set<iStreamListener> myStreamListeners = new HashSet<iStreamListener>();

  private String myId;
  private boolean isClosed = false;

  private ExecutorService myListenerService = Executors.newSingleThreadExecutor();
  
  private long myBytesSend = 0;
  private long myBytesReceived = 0;

  public StreamSplitter(InputStream aInputStream, OutputStream aOutputStream, iInputOutputHandler aInputOutputHandler) {
    super();
    myInputOutputHandler = aInputOutputHandler;
    myInputStream = new BufferedReader(new InputStreamReader(aInputStream));
    myOutputStream = new PrintWriter(new OutputStreamWriter(aOutputStream));
  }

  public void addStreamListener(iStreamListener aListener){
    myStreamListeners.add(aListener);
  }
  
  public void removeStreamListener(iStreamListener aListener){
    myStreamListeners.remove(aListener);
  }

  private void notifyListenersClosed(){
    for(iStreamListener theListener : new ArrayList<iStreamListener>(myStreamListeners)){
      theListener.streamClosed();
    }
  }

  private void notifyListenersOutgoingMessage(final String aMessage){
    myBytesSend += aMessage.length();
    myListenerService.execute(new Runnable(){
      public void run(){
        for(iStreamListener theListener : new ArrayList<iStreamListener>(myStreamListeners)){
          theListener.outgoingMessage(aMessage);
        }
      }
    });
  }

  private void notifyListenersIncoming(final String aMessage){
    myBytesReceived += aMessage.length();
    myListenerService.execute(new Runnable(){
      public void run(){
        for(iStreamListener theListener : new ArrayList<iStreamListener>(myStreamListeners)){
          theListener.incomingMessage(aMessage);
        }
      }
    });
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

  public void sendWithoutReply(String aMessage){
    notifyListenersOutgoingMessage(aMessage);
    myOutputStream.println(aMessage);
    myOutputStream.flush();
  }

  public String getId() {
    return myId;
  }

  public void setId( String aId ) {
    myId = aId;
  }
  
  public long getBytesSend() {
    return myBytesSend;
  }

  public long getBytesReceived() {
    return myBytesReceived;
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
          notifyListenersIncoming(theLine);
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
        notifyListenersClosed();
      }
    }
  }
}
