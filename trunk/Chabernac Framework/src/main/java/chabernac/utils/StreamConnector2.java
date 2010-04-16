package chabernac.utils;

import java.io.*;
import java.util.*;
import chabernac.utils.Debug;

public class StreamConnector2 extends Observable implements Runnable
{
  private InputStream  input = null;
  private OutputStream output = null;
  private boolean stop = false;

  public StreamConnector2(InputStream input, OutputStream output)
  {
    this.input = input;
    this.output = output;
  }

  public void run()
  {
    Debug.log(this,"Starting streamconnector...");
    if(input==null || output==null){stop = true;}
    int aByte;
    try
    {

	  while(!stop && (aByte = input.read()) != -1)
      {
		  output.write(aByte);
		  output.flush();
      }
    }catch(Exception e){Debug.log(this,"Exception occured in Streamconnector",e);}
     finally{ stop(); }
  }

  public synchronized void stop()
  {
    stop = true;
    setChanged();
    notifyObservers();
  }
  
  public void start(){
      stop = false;
      new Thread(this).start();
      setChanged();
      notifyObservers();
  }
  
  public boolean isStopped(){ return stop; }
}