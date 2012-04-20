package chabernac.protocol.routing;

public interface iWhoIsRunningListener {
  public void peerDetected(String aHost, int aPort, String aPeerId);
  public void noPeerAt(String aHost, int aPort);
}
