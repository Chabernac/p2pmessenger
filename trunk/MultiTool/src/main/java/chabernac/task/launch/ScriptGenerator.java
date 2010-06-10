package chabernac.task.launch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.log4j.Logger;


public class ScriptGenerator {
  private static Logger logger = Logger.getLogger(ScriptGenerator.class);
  public static void writeScripts(){
    extractSource(new File("light.cmd"), "scripts/light.cmd");
    extractSource(new File("service.cmd"), "scripts/service.cmd");
    extractSource(new File("heavy.cmd"), "scripts/heavy.cmd");
    extractSource(new File("launch.cmd"), "scripts/launch.cmd");
    extractSource(new File("compilefilter.cmd"), "scripts/compilefilter.cmd");
    File theFilterFile = new File("CustomFilter.class");
    if(!theFilterFile.exists()) extractSource(new File("CustomFilter.java"), "CustomFilter.java");
    extractAsIs(new File("SendFocus.exe"), "sendfocus/SendFocus.exe");
  }
  
  public static void extractAsIs(File aFile, String aLocation){
    InputStream theInputStream = null;
    OutputStream theOutputStream = null;
    int theByte;
    try{
      theOutputStream = new FileOutputStream(aFile);
      theInputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(aLocation);
       while((theByte = theInputStream.read()) != -1){
         theOutputStream.write(theByte);
       }
    } catch(IOException e){
      logger.error("Could not write file", e);
    } finally{
      if(theInputStream != null){
        try {
          theInputStream.close();
        } catch (IOException e) {
          logger.error("Could not close input stream", e);
        }
      }
      if(theOutputStream != null){
        try {
          theOutputStream.flush();
          theOutputStream.close();
        } catch (IOException e) {
          logger.error("Could not close output stream", e);
        }
      }
    }
  }
  
  public static void extractSource(File aFile, String aLocation){
    BufferedReader theReader = null;
    PrintWriter theWriter = null;
    try{
      theReader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream(aLocation)));
      theWriter= new PrintWriter(new FileWriter(aFile));
      //if(cd) theWriter.println("cd " + aFile.getAbsoluteFile().getParent());
      String theLine = null;
      while((theLine = theReader.readLine()) != null){
        theWriter.println(theLine);
      }
    } catch (IOException e) {
      logger.error("Could not create writer", e);
    }finally{
      if(theReader != null){
        try {
          theReader.close();
        } catch (IOException e) {
          logger.error("Could not close reader", e);
        }
      }
      if(theWriter != null){
        theWriter.flush();
        theWriter.close();
      }
    }
    
  }
  
  public static void main(String args[]){
    System.out.println(new File("out.log"));
    System.out.println(new File("out.log").getAbsoluteFile());
    System.out.println(new File("out.log").getAbsolutePath());
    System.out.println(new File("out.log").getName());
    System.out.println(new File("out.log").getAbsoluteFile().getParent());
  }
}
