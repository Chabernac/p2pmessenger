package chabernac.server;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import chabernac.utils.StreamConnector2;


public class CommandProtocol implements Protocol
{

  public void handle(InputStream inputStream, OutputStream outputStream) throws Exception
  {
      Runtime runtime = Runtime.getRuntime();
      BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
      PrintWriter out = new PrintWriter(outputStream,true);
      out.println("Hello");
      String input = "";
      while(!(input = in.readLine().toUpperCase()).equals("QUIT"))
      {
        if(input.equals("EXEC"))
        {
          Process process = null;
          BufferedReader processIn = null;
          PrintWriter processOut = null;

          try
          {
            process = runtime.exec(in.readLine());
            //processIn = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StreamConnector2 connector = new StreamConnector2(process.getInputStream(),outputStream);
            new Thread(connector).start();
            processOut = new PrintWriter(process.getOutputStream(),true);
            while(!(input = in.readLine()).toUpperCase().equals("STOPPROCESS"))
            {
              //System.out.println(input);
              processOut.println(input);
            }
            connector.stop();
          }catch(Exception e){out.println("Error while executing process: " + e);}
           finally
           {
             try
             {
               if(process!=null)process.destroy();
               if(processIn!=null)processIn.close();
               if(processOut!=null)processOut.flush();
               if(processOut!=null)processOut.close();
             }catch(Exception e){System.out.println("Error while closing processstreams: " + e);}
           }

          out.println("Process destroyed");
        }
      }
      out.println("Bye");

  }

}