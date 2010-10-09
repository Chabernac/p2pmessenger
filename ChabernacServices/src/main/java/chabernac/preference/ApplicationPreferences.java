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
      if(myProperties != null){
        myProperties.store(new FileOutputStream(myFile), "Application preferences");
      }
    } catch (FileNotFoundException e) {
      logger.error("ould not write to preference file", e);
    } catch (IOException e) {
      logger.error("Could not write to preference file", e);
    }
  }
  
  public void setEnumProperty(Enum anEnum){
    setProperty(anEnum.getClass().getName(), anEnum.toString());
  }
  
  public boolean hasEnumProperty(Enum anEnum){
    return anEnum.toString().equals(getProperty(anEnum.getClass().getName()));
  }
  
  public boolean hasEnumType(Class<? extends Enum> anEnumType){
    return containsKey(anEnumType.getName());
  }

  public boolean hasEnumProperty(Enum anEnum, Enum aDefault){
    if(!containsKey(anEnum.getClass().getName())) return anEnum.equals(aDefault);
    return anEnum.toString().equals(getProperty(anEnum.getClass().getName()));
  }
}
