package chabernac.server;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

public class EchoProtocol implements Protocol
{
  public void handle(InputStream inputStream, OutputStream outputStream) throws Exception
  {
	  BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
	  PrintWriter out = new PrintWriter(outputStream,true);
	  out.println("HELLO");
	  String line = null;
	  while(!(line = in.readLine()).toUpperCase().equals("QUIT"))
	  {
		  out.println(line);
	  }
	  out.println("BYE");
  }
}