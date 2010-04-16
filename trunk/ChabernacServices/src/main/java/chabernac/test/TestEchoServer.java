package chabernac.test;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import chabernac.nserver.Client;
import chabernac.nserver.Protocol;

public class TestEchoServer {

  public static void main(String[] args) {
    Client theClient = new Client("localhost", 14003, new ClientProtocol());
    theClient.start();
    
  }
  
  private static class ClientProtocol implements Protocol{

    public void handle(Socket aSocket) throws Exception {
      InputStream anInput = aSocket.getInputStream();
      OutputStream anOutput = aSocket.getOutputStream();
      anOutput.write("hello world".getBytes());
      int theByte;
      while((theByte = anInput.read()) != -1){
        System.out.print((char)theByte);
      }
    }
  }

}
