package chabernac.utils;

import java.io.*;

public class StreamConnector implements Runnable
{
  private BufferedReader input = null;
  private PrintWriter output = null;
  private boolean stop = false;

  public StreamConnector(InputStream input, OutputStream output)
  {
    this.input = new BufferedReader(new InputStreamReader(input));
    this.output = new PrintWriter(output);
  }

  public void run()
  {
    if(input==null || output==null){stop = true;}
    String line = "";
    try
    {
      while(!stop && (line = input.readLine())!=null)
      {
        output.println(line);
        output.flush();
      }
    }catch(Exception e){Debug.log(this,"Exception occured in Streamconnector: ", e);}
  }
  
  public void start(){
      new Thread(this).start();
  }

  public void stop()
  {
    stop = true;
  }
}