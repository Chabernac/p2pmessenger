package chabernac.server;

import java.net.Socket;

public interface iProtocol {
  public void handle(Socket aSocket);

}
