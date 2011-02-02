package chabernac.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.log4j.Logger;

public class IOTools {
	private static Logger logger = Logger.getLogger(IOTools.class);

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

	public static void saveObject(Object anObject, File aFile) throws IOException{
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

	private static void saveSerializable(Serializable anObject, File aFile) throws IOException{

		ObjectOutputStream theOutputStream = null;
		File theTempFile = null;
		try{
			theTempFile = File.createTempFile("serialize", ".bin");
			theOutputStream = new ObjectOutputStream(new FileOutputStream(theTempFile));
			theOutputStream.writeObject(anObject);

		}finally{
			if(theOutputStream != null){
				theOutputStream.flush();
				theOutputStream.close();
			}
		}
		copyFile(theTempFile, aFile);
		theTempFile.delete();
	}


	public static void copyFile(File in, File out) throws IOException {
		FileInputStream fis  = new FileInputStream(in);
		FileOutputStream fos = new FileOutputStream(out);
		
		copyStream( fis, fos );
	}
	
	public static void copyStream(InputStream anInput, OutputStream anOutput) throws IOException{
	  try {
      byte[] buf = new byte[1024];
      int i = 0;
      while ((i = anInput.read(buf)) != -1) {
        anOutput.write(buf, 0, i);
      }
    } finally {
      if (anInput != null) {
        anOutput.close();
      }
      if (anOutput != null) {
        anOutput.flush();
        anOutput.close();
      }
    }
	}

	public static byte[] readInputStream(InputStream aStream) throws IOException{
		byte[] theBuffer = new byte[1024];
		byte[] theBytes = new byte[0];
		int read = 0;
		while((read = aStream.read(theBuffer)) != -1){
			byte[] theNewBytes = new byte[theBytes.length + read];
			System.arraycopy(theBytes, 0, theNewBytes, 0, theBytes.length);
			System.arraycopy(theBuffer, 0, theNewBytes, theBytes.length, read);
			theBytes = theNewBytes;
		}
		return theBytes;
	}




//	public static byte[] readInputStream(InputStream aStream, int aNrOfBytes){

//	}

	public static void main(String[] args){
		try {
			byte[] theBytes = readInputStream(IOTools.class.getClassLoader().getResourceAsStream("chabernac/utils/IOTools.class"));
			System.out.println(new String(theBytes));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
