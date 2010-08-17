package chabernac.lock;

import java.io.File;

public class FileLock implements iLock {
  private final File myLockFile;
  
  public FileLock(String anApplicationName){
    myLockFile = new File(anApplicationName + ".lock");
  }

  @Override
  public boolean createLock() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isLocked() {
    // TODO Auto-generated method stub
    return false;
  }

}
