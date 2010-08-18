package chabernac.lock;

public interface iLock {
  /**
   * return if the lock exists
   */
  public boolean isLocked();
  
  /**
   * create the lock, return true if succeeded
   */
  public boolean createLock();
  
  /**
   * remove the lock
   * return true if the removal of the lock was ok
   * @return
   */
  public boolean removeLock();
}
