package chabernac.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

public class DataFile implements Serializable{
	//public static final long serialVersionUID = 5583761086855705942L;
  public static final long serialVersionUID = 5581095698458361984L;
  
	
//	private static Logger logger = Logger.getLogger(DataFile.class);
	private String myFileName = null;
	private String myDestination = "received";
	private byte[] myBytes = null;
	
	
	public DataFile(String aFileName){
		myFileName = aFileName;
	}
	
	public void setData(byte[] data){
		myBytes = data;
	}
	
	public byte[] getData(){
		return myBytes;
	}
	
	public void setFileName(String aFileName){
		myFileName = aFileName;
	}
	
	public String getFileName(){
		return myFileName;
	}
	
	public String getDestination() {
		return myDestination;
	}
	
	public void setDestination(String aDestination) {
		myDestination = aDestination;
	}
	
	public File getFile(){
		return new File(getDestination() + "\\" + getFileName());
	}
	
	public static DataFile loadFromFile(File aFile){
		byte[] data = null;
		FileInputStream theStream = null;
		try{
			theStream = new FileInputStream(aFile);
			data = new byte[0];
			int available = 0;
			while( (available = theStream.available()) > 0){
				byte[] theData = new byte[data.length + available];
				System.arraycopy(data, 0, theData, 0, data.length);
				theStream.read(theData, data.length, available);
				data = theData;
			}
		}catch(FileNotFoundException e){
//			logger.error("Could not find file: " + aFile.toString(), e);
			return null;
		} catch (IOException e) {
//			logger.error("Error occured while reading file: " + aFile.toString(), e);
			return null;
		} finally {
			if(theStream != null){
				try {
					theStream.close();
				} catch (IOException e) {
//					logger.error("Could not close inputstream", e);
				}
			}
		}
		DataFile theFile = new DataFile(aFile.getName());
		theFile.setData(data);
		return theFile;
	}
	
	public String toString(){
		return myFileName;
	}
	
	public void finalize(){
		myBytes = null;
		myFileName = null;
	}
	
	
}
