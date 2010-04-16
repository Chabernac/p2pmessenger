package chabernac.data;

import java.util.*;
import java.io.*;
import chabernac.utils.*;

public class SavedList implements Runnable{
	private Collection myCollection = null;
	private File myFile = null;
	private int myUpdates = 0;
	private ObjectStringConvertor myConvertor = null;
	private int myInterval;
	private boolean running = false;

	public SavedList(Collection aCollection, File aFile, ObjectStringConvertor aConvertor){
		this(aCollection, aFile, aConvertor, -1);
	}

	public SavedList(Collection aCollection, File aFile, ObjectStringConvertor aConvertor, int aInterval){
		myCollection = aCollection;
		myFile = aFile;
		myConvertor = aConvertor;
		//loadFile();
		setInterval(aInterval * 60000);
	}

	public void loadFile(){
		if(!myFile.exists()) return;
		myCollection.clear();
		BufferedReader theReader = null;
		try{
			theReader = new BufferedReader(new FileReader(myFile));
			String line = null;
			while((line = theReader.readLine()) != null){
				myCollection.add(myConvertor.convertToObject(line));
			}
		}catch(Exception e){
			Debug.log(this,"Could not load file: " + myFile.toString(), e);
		}finally{
			if(theReader != null){
				try{
					theReader.close();
				}catch(Exception e){ Debug.log(this,"Could not close inputStream",e); }
			}
		}

	}

	public void writeFile(){
		PrintWriter thePrintWriter = null;
		try{
			thePrintWriter = new PrintWriter(new FileOutputStream(myFile));
			Iterator theIterator = myCollection.iterator();
			while(theIterator.hasNext()){
				thePrintWriter.println(myConvertor.convertToString(theIterator.next()));
			}
		}catch(Exception e){
			Debug.log(this,"Could not write list to file: " + myFile.toString(), e);
		}finally{
			if(thePrintWriter != null){
				try{
					thePrintWriter.flush();
					thePrintWriter.close();
				}catch(Exception e){ Debug.log(this,"Could not close outputstream",e); }
			}
		}
	}

	public void add(Object aObject){
		myUpdates++;
		myCollection.add(aObject);
	}

	public void remove(Object aObject){
		myUpdates++;
		myCollection.remove(aObject);
	}

	public void setInterval(int aInterval){
		myInterval = aInterval;
		if(myInterval > 0 && !running){
			new Thread(this).start();
		}
	}
	public int getInterval(){
		return myInterval;
	}

	public Collection getCollection(){
		return myCollection;
	}

	public void run(){
		try{
			while(myInterval > 0){
					running = true;
					Thread.sleep(myInterval);
					writeFile();
			}
		}catch(Exception e){ Debug.log(this,"Could not sleep",e);	}
		 finally{
			running = false;
		}
	}


}
