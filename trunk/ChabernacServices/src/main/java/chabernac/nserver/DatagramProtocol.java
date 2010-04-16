package chabernac.nserver;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public interface DatagramProtocol {
  public void handle(DatagramSocket aSocket, DatagramPacket aPacket);

}
