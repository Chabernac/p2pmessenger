/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.backup;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

public class BackupFileTest extends TestCase {
  public void testBackup() throws IOException{
    File theFileToBackup = new File("c:\\data\\a.txt");
    assertTrue( theFileToBackup.createNewFile() );
    
    try{
      BackupFile theBackup = new BackupFile();
      theBackup.setBackupLocation( new File("c:\\temp") );
      theBackup.setFile( theFileToBackup );
      theBackup.run();
    } finally {
      theFileToBackup.delete();
    }
    
  }
}
