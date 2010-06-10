package chabernac.preference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ApplicationPreferences extends Properties{
  private static Logger logger = Logger.getLogger(ApplicationPreferences.class);
  private static ApplicationPreferences myProperties = null;
  private static File myFile = new File("properties.txt");
  
  public static ApplicationPreferences getInstance(){
    if(myProperties == null){
      myProperties = new ApplicationPreferences();
      if(myFile.exists()){
        try{
          myProperties.load(new FileInputStream(myFile));
        }catch(IOException e){
          logger.error("Could not load preferences", e);
        }
      }
    }
    return myProperties;
  }
  
  public static void save(){
    try {
      myProperties.store(new FileOutputStream(myFile), "Properties for Activity Logger");
    } catch (FileNotFoundException e) {
      logger.error("ould not write to preference file", e);
    } catch (IOException e) {
      logger.error("Could not write to preference file", e);
    }
  }

  
}
