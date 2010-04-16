package chabernac.nserver;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;

public class EchoAllBytesProtocol implements Protocol {
  private ArrayList myOutputStreams = null;
   
  public EchoAllBytesProtocol(){
    myOutputStreams = new ArrayList();
  }
  
  public void handle(Socket aSocket) throws Exception {
    InputStream anInput = aSocket.getInputStream();
    OutputStream anOutput = aSocket.getOutputStream();
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
    byte[] theBytes = new byte[1024];
    int j=0;
    while( (theByte = anInput.read()) != -1){
      theBytes[j++] = (byte)theByte;
      if(theByte == 0x00){
        for(Iterator i=myOutputStreams.iterator();i.hasNext();){
          OutputStream theStream = (OutputStream)i.next();
          //don't copy the bytes where they came from
          if(theStream != anOutput){
            //synchronize on the stream so that bytes of different players don't get mixed up
            synchronized (theStream) {
              try{
                theStream.write(theBytes, 0, j);
              }catch(SocketException e){
                //myOutputStreams.remove(theStream);
              }  
            }
          }
        }
        j = 0;
      }
    }
    myOutputStreams.remove(anOutput);
  }

}
