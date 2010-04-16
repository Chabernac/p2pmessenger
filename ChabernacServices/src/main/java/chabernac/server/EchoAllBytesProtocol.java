package chabernac.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;

public class EchoAllBytesProtocol implements Protocol {
  private ArrayList myOutputStreams = null;
   
  public EchoAllBytesProtocol(){
    myOutputStreams = new ArrayList();
  }
  
  public void handle(InputStream anInput, OutputStream anOutput) throws Exception {
    myOutputStreams.add(anOutput);
//    int available = 0;
//    while( (available = anInput.available()) > 0){
//      System.out.println("Echoing " + available + " bytes");
//      byte[] theBytes = new byte[available];
//      anInput.read(theBytes);
//      for(Iterator i=myOutputStreams.iterator();i.hasNext();){
//        ((OutputStream)i.next()).write(theBytes);
//      }
//    }
    int theByte;
    while( (theByte = anInput.read()) != -1){
      for(Iterator i=myOutputStreams.iterator();i.hasNext();){
    	  OutputStream theStream = (OutputStream)i.next();
    	  try{
    		  theStream.write(theByte);
    	  }catch(SocketException e){
    		  //myOutputStreams.remove(theStream);
    	  }
      }
    }
    myOutputStreams.remove(anOutput);
  }

}
