package chabernac.io;

public interface iStreamListener {
  public void streamClosed();
  public void incomingMessage(String aMessage);
  public void outgoingMessage(String aMessage);
}
