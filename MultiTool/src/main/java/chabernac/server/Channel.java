package chabernac.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.log4j.Logger;


public class Channel {
  private static Logger logger = Logger.getLogger(Channel.class);
  private BufferedReader bufferedReader = null;
  private OutputStream outputStream = null;
  
  public Channel(InputStream anInputStream, OutputStream anOutputStream){
    bufferedReader = new BufferedReader(new InputStreamReader(anInputStream));
    outputStream = anOutputStream;
  }
  
  public synchronized String read(){
    try{
      return bufferedReader.readLine();
    }catch(IOException e){
      logger.error("Could not read line", e);
      return null;
    }
  }
  
  public synchronized void write(String aString){
	  write((aString.trim() + "\r\n").getBytes());
  }
  
  public synchronized void write(byte[] bytes){
    try{
      outputStream.write(bytes);
      outputStream.flush();
    }catch(IOException e){
      logger.error("Could not write to channel", e);
    }
  }
  
  public synchronized void close(){
    try {
      bufferedReader.close();
    } catch (IOException e) {
      logger.error("Could not close reader", e);
    }
    try {
      outputStream.flush();
    } catch (IOException e1) {
      logger.error("Could not flush writer", e1);
    }
    try {
      outputStream.close();
    } catch (IOException e2) {
      logger.error("Could not close writer", e2);
    }
  }

}
