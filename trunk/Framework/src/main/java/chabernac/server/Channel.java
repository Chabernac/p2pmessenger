package chabernac.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import chabernac.log.Logger;

public class Channel {
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
      Logger.log(this,"Could not read line");
      return null;
    }
  }
  
  public synchronized void write(String aString){
    try{
      outputStream.write((aString.trim() + "\r\n").getBytes());
      outputStream.flush();
    }catch(IOException e){
      Logger.log(this, "Could not write to channel", e);
    }
  }
  
  public synchronized void close(){
    try {
      bufferedReader.close();
    } catch (IOException e) {
      Logger.log(this,"Could not close reader", e);
    }
    try {
      outputStream.flush();
    } catch (IOException e1) {
      Logger.log(this,"Could not flush writer", e1);
    }
    try {
      outputStream.close();
    } catch (IOException e2) {
      Logger.log(this,"Could not close writer", e2);
    }
  }

}
