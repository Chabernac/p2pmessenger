package chabernac.lock;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileLock implements iLock {
  private java.nio.channels.FileLock myFileLock;
  private RandomAccessFile myRandomAccessFile = null;
  private final File myLockFile;

  public FileLock(String anApplicationName){
    myLockFile = new File(anApplicationName + ".lock");
    Runtime.getRuntime().addShutdownHook( new Thread(new Runnable(){
      public void run(){
       removeLock(); 
      }
    }));
  }

  @Override
  public boolean createLock() {
    try{

      myRandomAccessFile = new RandomAccessFile(myLockFile, "rw");
      myFileLock = myRandomAccessFile.getChannel().tryLock();

      myRandomAccessFile.write( Long.toString( System.currentTimeMillis() ).getBytes() );

      return true;
    }catch(IOException e){
      return false;
    }
  }

  @Override
  public boolean isLocked() {
    RandomAccessFile theRandomAccessFile = null;
    try{
      theRandomAccessFile = new RandomAccessFile(myLockFile, "rw");
      theRandomAccessFile.write( Long.toString( System.currentTimeMillis() ).getBytes() );
      return false;
    }catch(Exception e){
      return true;
    } finally {
      if(theRandomAccessFile != null){
        try {
          theRandomAccessFile.close();
        } catch ( IOException e ) {
        }
      }
    }
  }

  @Override
  public boolean removeLock() {

    if(myFileLock != null){
      try {
        myFileLock.release();
      } catch ( IOException e ) {
        return false;
      }
    }
    
    if(myRandomAccessFile != null){
      try {
        myRandomAccessFile.close();
      } catch ( IOException e ) {
        return false;
      }
    }
    
    if(myLockFile.exists()){
      myLockFile.delete();
    }
    
    return !myLockFile.exists();
  }

}
