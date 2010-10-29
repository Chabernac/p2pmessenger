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
	private static final long serialVersionUID = 5592059303424276001L;
	private static Logger LOGGER = Logger.getLogger(ApplicationPreferences.class);
	private static File FILE = new File("properties.txt");
	
	private static ApplicationPreferences INSTANCE = null;

	private List< iApplicationPreferenceListener > myListeners = new ArrayList< iApplicationPreferenceListener >();

	public static ApplicationPreferences getInstance(){
		if(INSTANCE == null){
			INSTANCE = new ApplicationPreferences();
			if(FILE.exists()){
				try{
					INSTANCE.load(new FileInputStream(FILE));
				}catch(IOException e){
					LOGGER.error("Could not load preferences", e);
				}
			}
		}
		return INSTANCE;
	}

	public static void save(){
		try {
			if(INSTANCE != null){
				INSTANCE.store(new FileOutputStream(FILE), "Application preferences");
			}
		} catch (FileNotFoundException e) {
			LOGGER.error("ould not write to preference file", e);
		} catch (IOException e) {
			LOGGER.error("Could not write to preference file", e);
		}
	}

	public synchronized void setEnumProperty(Enum anEnum){
		setProperty(anEnum.getClass().getName(), anEnum.toString());
		notifyListeners( anEnum );
	}

	public synchronized Object setProperty(String aKey, String aValue){
		Object theResult = super.setProperty( aKey, aValue );
		notifyListeners( aKey );
		return theResult;
	}

	public synchronized boolean hasEnumProperty(Enum anEnum){
		return anEnum.toString().equals(getProperty(anEnum.getClass().getName()));
	}

	public synchronized boolean hasEnumType(Class<? extends Enum> anEnumType){
		return containsKey(anEnumType.getName());
	}

	public synchronized boolean hasEnumProperty(Enum anEnum, Enum aDefault){
		if(!containsKey(anEnum.getClass().getName())) return anEnum.equals(aDefault);
		return anEnum.toString().equals(getProperty(anEnum.getClass().getName()));
	}

	public void addApplicationPreferenceListener(iApplicationPreferenceListener aListener){
		myListeners.add(aListener);
	}

	public synchronized void removeApplicationPreferenceListener(iApplicationPreferenceListener aListener){
		myListeners.remove(aListener);
	}

	public synchronized void notifyListeners(String aChangedKey){
		for(iApplicationPreferenceListener theListener : myListeners){
			theListener.applicationPreferenceChanged( aChangedKey, getProperty( aChangedKey ) );
		}
	}

	public synchronized void notifyListeners(Enum aChangedEnum){
		for(iApplicationPreferenceListener theListener : myListeners){
			theListener.applicationPreferenceChanged( aChangedEnum );
		}
	}
}
