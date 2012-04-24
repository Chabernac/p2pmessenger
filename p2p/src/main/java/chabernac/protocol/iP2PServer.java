package chabernac.protocol;

public interface iP2PServer {
  public boolean start();
  public boolean isStarted();
  
  /**
   * execute a clean stop
   */
  public void stop();
  
  /**
   * execute a fast kill without doing clean up stuff
   */
  public void kill();
  
  public void showDebugInfo();
  
}
