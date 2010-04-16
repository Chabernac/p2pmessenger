package chabernac.synchro;

import chabernac.nserver.EchoAllBytesProtocol;
import chabernac.nserver.Server;

public class SynchronizationServer {

  public static void main(String[] args) {
    Server theServer = new Server(14003, new EchoAllBytesProtocol());
    theServer.start();
  }

}
