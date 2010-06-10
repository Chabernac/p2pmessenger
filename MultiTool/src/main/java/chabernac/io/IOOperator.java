/*
 * Created on 12-feb-2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package chabernac.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.log4j.Logger;

import chabernac.command.CommandTimer;
import chabernac.util.Tools;

public class IOOperator {
	private static Logger logger = Logger.getLogger(IOOperator.class);

	public static Object loadObject(File aFile){
		if(!aFile.exists()) return null;
		ObjectInputStream theStream = null;
		try{
			theStream = new ObjectInputStream(new FileInputStream(aFile));
			return theStream.readObject();
		}catch(ClassNotFoundException e){
			logger.error("could not load object class", e);
		}catch(IOException e){
			logger.error("could not open inpustream", e);
		}finally{
			if(theStream != null){
				try{
					theStream.close();
				}catch(IOException e){
					logger.error("Couldn't not close inputstream", e);
				}
			}
		}
		return null;
	}

	public static Hashtable loadProperties(File aFile){
		Properties theUsers = new Properties();
		if(aFile.exists()){
			try{
				theUsers.load(new FileInputStream(aFile));
			}catch(FileNotFoundException e){
				logger.error("user file not found", e);
			} catch (IOException e) {
				logger.error("Could not load user list", e);
			}
		}
		return theUsers;
	}

	public static void saveObject(Object anObject, File aFile){
		if(anObject instanceof Properties) saveProperties((Properties)anObject, aFile);
		else if(anObject instanceof Serializable) saveSerializable((Serializable)anObject, aFile);
	}

	private static void saveProperties(Properties properties, File aFile) {
		try {
			properties.store(new FileOutputStream(aFile), "");
		} catch (FileNotFoundException e) {
			logger.error("could not store properties", e);
		} catch (IOException e) {
			logger.error("could not store properties", e);
		}
	}

	private static void saveSerializable(Serializable anObject, File aFile){
		ObjectOutputStream theOutputStream = null;
		try{
			theOutputStream = new ObjectOutputStream(new FileOutputStream(aFile));
			theOutputStream.writeObject(anObject);
		}catch(IOException e){
			logger.error("could not save object", e);
		}finally{
			if(theOutputStream != null){
				try{
					theOutputStream.close();
				}catch(IOException e){
					logger.error("Could not flush output stream", e);
				}
				try{
					theOutputStream.close();
				}catch(IOException e){
					logger.error("Could not close output stream", e);
				}
			}
		}
	}

	public static void saveObjectEveryXMs(Object anObject, File aFile, long aTimeout){
		new CommandTimer(new SaveObjectCommand(anObject, aFile), aTimeout);
	}




}
