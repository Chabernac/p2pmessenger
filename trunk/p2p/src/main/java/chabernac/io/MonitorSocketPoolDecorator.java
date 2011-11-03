package chabernac.io;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.List;
import java.util.Observer;

public class MonitorSocketPoolDecorator implements iSocketPool {

  @Override
  public SocketProxy checkOut(SocketAddress anAddress) throws IOException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void checkIn(SocketProxy aSocket) {
    // TODO Auto-generated method stub

  }

  @Override
  public void close(SocketProxy aSocket) {
    // TODO Auto-generated method stub

  }

  @Override
  public void cleanUp() {
    // TODO Auto-generated method stub

  }

  @Override
  public void cleanUpOlderThan(long aTimestamp) {
    // TODO Auto-generated method stub

  }

  @Override
  public List<SocketProxy> getCheckedInPool() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<SocketProxy> getCheckedOutPool() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<SocketProxy> getConnectingPool() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void addObserver(Observer anObserver) {
    // TODO Auto-generated method stub

  }

  @Override
  public void deleteObserver(Observer anObserver) {
    // TODO Auto-generated method stub

  }

}
