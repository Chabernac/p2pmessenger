package chabernac.lock;

public interface iLock {
  /*
   * return if the lock exists
   */
  public boolean isLocked();
  
  /*
   * create the lock, return true if succeeded
   */
  public boolean createLock();
}
