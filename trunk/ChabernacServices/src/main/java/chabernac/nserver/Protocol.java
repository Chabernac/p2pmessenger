package chabernac.nserver;

import java.net.Socket;

public interface Protocol
{
  public void handle(Socket aSocket) throws Exception;
}