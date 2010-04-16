package chabernac.nserver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class EchoProtocol implements Protocol
{
  public void handle(Socket aSocket) throws Exception
  {
    InputStream inputStream = aSocket.getInputStream();
    OutputStream outputStream = aSocket.getOutputStream();
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