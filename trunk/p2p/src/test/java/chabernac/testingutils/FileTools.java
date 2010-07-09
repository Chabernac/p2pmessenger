/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.testingutils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileTools {

  /**
   * create a file with random content of the given bytes length
   * 
   * @param aFile
   * @param aBytes
   */
  public static void createFile(File aFile, long aBytes) throws IOException{
    OutputStream theOutputStream = null;
    try{
      theOutputStream = new FileOutputStream(aFile);
      
      byte[] theBytes = new byte[1024];
      for(int i=0;i<theBytes.length;i++){
        theBytes[i] = (byte)i;
      }
      
      long theBytesWritten = 0;
      while(theBytesWritten < aBytes){
        long theWriteNowLength = aBytes - theBytesWritten;
        if(theWriteNowLength > theBytes.length){
          theWriteNowLength = theBytes.length;
        }
        theOutputStream.write( theBytes, 0, (int)theWriteNowLength );
        theBytesWritten+= theWriteNowLength;
      }
     
    }finally{
      if(theOutputStream != null){
        theOutputStream.close();
      }
    }
  }
}
