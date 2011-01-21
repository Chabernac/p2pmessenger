/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.backup;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import chabernac.utils.IOTools;

public class BackupFile implements Runnable{
  private static Logger LOGGER = Logger.getLogger(BackupFile.class);

  private File myFile;
  private File myBackupLocation;
  private int myNumberOfBackups;

  private SimpleDateFormat TIME_FORMAT = new SimpleDateFormat( "yyyyMMddHHmmss");

  public void run(){
    if(myFile.exists()){
      String theFile = myFile.getName();
      int theDotIndex = theFile.lastIndexOf( "." );
      String theFileName = theFile.substring( 0,  theDotIndex);
      String theExtention = theFile.substring( theDotIndex + 1, theFile.length() );
      
      File theNewFile = new File(myBackupLocation, theFileName + "_" + TIME_FORMAT.format( new Date()) + "." + theExtention);
      try{
        theNewFile.createNewFile();
        IOTools.copyFile( myFile, theNewFile );
      }catch(IOException e){
        LOGGER.error("Unable to create backup", e);
      }
    }
  }

  public File getFile() {
    return myFile;
  }

  public void setFile( File aFile ) {
    myFile = aFile;
  }

  public File getBackupLocation() {
    return myBackupLocation;
  }

  public void setBackupLocation( File aBackupLocation ) {
    myBackupLocation = aBackupLocation;
  }

  public int getNumberOfBackups() {
    return myNumberOfBackups;
  }

  public void setNumberOfBackups( int aNumberOfBackups ) {
    myNumberOfBackups = aNumberOfBackups;
  }
  
  
}
