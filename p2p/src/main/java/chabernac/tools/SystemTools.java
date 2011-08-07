package chabernac.tools;

import java.io.File;
import java.io.IOException;

public class SystemTools {
  public static void openFile(File aFile) throws IOException{
    Runtime theRuntime = Runtime.getRuntime();
    //TODO make OS generic
    theRuntime.exec("cmd /c " + aFile.getAbsolutePath());
  }
  
  public static void openDirectory(File aFile) throws IOException{
    if(!aFile.isDirectory()){
      aFile = aFile.getParentFile();
    }
    Runtime theRuntime = Runtime.getRuntime();
    //TODO make OS generic
    theRuntime.exec("explorer " + aFile.getAbsolutePath());
  }
}
