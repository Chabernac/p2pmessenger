/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOTools {
  public void writeObjectToFileAsXML(File aFile, Object anObject){
    //TODO implement writeObjectToFile
  }
  
  public Object loadObjectFromFileAsXML(File aFile){
    //TODO implement loadObjectFromFile
    return null;
  }
  
  public static void copyStream(InputStream anInputStream, OutputStream anOutputStream) throws IOException{
    byte[] theBytes = new byte[1024];
    int length;
    while((length = anInputStream.read(theBytes)) != -1){
      anOutputStream.write(theBytes, 0, length);
    }
  }
}
