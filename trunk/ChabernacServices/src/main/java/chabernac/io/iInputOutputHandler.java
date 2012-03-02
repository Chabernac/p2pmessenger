package chabernac.io;

public interface iInputOutputHandler {
  public String handle(String anId, String anInput);
  public void close();
}
