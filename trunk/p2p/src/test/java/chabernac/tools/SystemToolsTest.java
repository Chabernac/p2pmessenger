package chabernac.tools;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

public class SystemToolsTest extends TestCase {
  public void testOpenFile() throws IOException{
    //TODO make a good test
    SystemTools.openFile(new File("c:\\temp\\a.txt"));
  }
  
  public void testOpenDirectory() throws IOException{
  //TODO make a good test
    SystemTools.openDirectory(new File("c:\\temp\\a.txt"));
  }
}
