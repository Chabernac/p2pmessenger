package chabernac.io;

public interface iInputOutputHandler {
  
  /**
   * the implementation must handle the input coming from the remote id and send a reply
   */
  public String handle(String aRemoteId, String anInput);
  
  
  public void close();
}
