package chabernac.preference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ApplicationPreferences extends Properties{
  private static Logger logger = Logger.getLogger(ApplicationPreferences.class);
  private static ApplicationPreferences myProperties = null;
  private static File myFile = new File("properties.txt");
  
  private List< iApplicationPreferenceListener > myListeners = new ArrayList< iApplicationPreferenceListener >();
  
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
    notifyListeners( anEnum );
  }
  
  public Object setProperty(String aKey, String aValue){
    Object theResult = super.setProperty( aKey, aValue );
    notifyListeners( aKey );
    return theResult;
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
  
  public void addApplicationPreferenceListener(iApplicationPreferenceListener aListener){
    myListeners.add(aListener);
  }
  
  public void removeApplicationPreferenceListener(iApplicationPreferenceListener aListener){
    myListeners.remove(aListener);
  }
  
  private void notifyListeners(String aChangedKey){
    for(iApplicationPreferenceListener theListener : myListeners){
      theListener.applicationPreferenceChanged( aChangedKey, getProperty( aChangedKey ) );
    }
  }
  
  private void notifyListeners(Enum aChangedEnum){
    for(iApplicationPreferenceListener theListener : myListeners){
      theListener.applicationPreferenceChanged( aChangedEnum );
    }
  }
}
