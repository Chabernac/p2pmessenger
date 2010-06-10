package chabernac.distributionservice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import chabernac.command.Command;
import chabernac.io.DataFile;

public class DistributionCommand implements Command, Serializable {
  public static final long serialVersionUID = 8312182220588605243L;
 // private static Logger logger = Logger.getLogger(DistributionCommand.class);
  
  private ArrayList myDataFiles = null;
  
  public DistributionCommand(String aJarLocation){
    loadFiles(ClassLoader.getSystemClassLoader().getResourceAsStream(aJarLocation));
  }
  
  public DistributionCommand(File aDistributionList){
    try {
      loadFiles(new FileInputStream(aDistributionList));
    } catch (FileNotFoundException e) {
    	e.printStackTrace();
      //logger.error("An error occured while opening distributionlist", e);
    }
  }
  
  private void loadFiles(InputStream anInputStream){
    myDataFiles = new ArrayList();
    BufferedReader theReader = null;
    try {
      theReader = new BufferedReader(new InputStreamReader(anInputStream));
      String theLine = "";
      while((theLine = theReader.readLine()) != null){
        loadDataFile(new File(theLine));
      }
    } catch (FileNotFoundException e) {
    	e.printStackTrace();
      //logger.error("An error occured while opening distributionlist", e);
    } catch (IOException e) {
    	e.printStackTrace();
      //logger.error("An error occured while reading distributionlist", e);
    } finally {
      if(theReader != null){
        try {
          theReader.close();
        } catch (IOException e) {
        	e.printStackTrace();
          //logger.error("Could nog close reader", e);
        }
      }
    }
  }
  
  private void loadDataFile(File aFile){
    DataFile theFile = DataFile.loadFromFile(aFile);
    theFile.setFileName(aFile.getPath());
    myDataFiles.add(theFile);
  }

  public void execute() {
    if(myDataFiles == null) return;
    for(Iterator i=myDataFiles.iterator();i.hasNext();){
      DataFile theFile = (DataFile)i.next();
      FileOutputStream theStream = null;
      try {
        theStream =new FileOutputStream(new File(theFile.getFileName()));
        theStream.write(theFile.getData());
      } catch (FileNotFoundException e) {
    	  e.printStackTrace();
        //logger.error("file not found", e);
      } catch (IOException e) {
    	  e.printStackTrace();
        //logger.error("could not write datafile", e);
      } finally{
        if(theStream != null){
          try {
            theStream.flush();
            theStream.close();
          } catch (IOException e) {
        	  e.printStackTrace();
            //logger.error("Could not close stream",e);
          }
        }
      }
    }
  }
}
