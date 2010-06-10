
package chabernac.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class IOManager {
  private File myFile = null;
  private ObjectStringConvertor myConvertor = null;
  
  public IOManager(File aFile, ObjectStringConvertor aConvertor){
    myFile = aFile;
    myConvertor = aConvertor;
  }
  
  public boolean save(Object anObject){
    return saveAs(anObject, myFile);
  }
  
  public boolean saveAs(Object anObject, File aFile){
    String theStringRepresentation = myConvertor.convertToString(anObject);
    FileOutputStream theStream = null;
    try{
      theStream = new FileOutputStream(aFile);
      theStream.write(theStringRepresentation.getBytes());
      myFile = aFile;
    }catch(IOException e){
      //Debug.log(this, "Could not save object", e);
      return false;
    }finally{
      if(theStream != null){
        try{
          theStream.close();
        }catch(IOException e){
          //Debug.log(this,"Could not close stream", e);
        }
      }
    }
    return true;
  }
  
  public Object reload(){
    return loadFrom(myFile);
  }
  
  public Object loadFrom(File aFile){
    BufferedReader theInputStream = null;
    try{
      theInputStream = new BufferedReader(new InputStreamReader(new FileInputStream(aFile)));
      String theLine = null;
      String theStringRepresentation = "";
      while((theLine = theInputStream.readLine()) != null){
        theStringRepresentation += theLine + "\n";
      }
      return myConvertor.convertToObject(theStringRepresentation);
    }catch(IOException e){
      return null;
    }finally{
      if(theInputStream != null)
        try{
          theInputStream.close();
        }catch(IOException e){
          //Debug.log(this,"Could not close input stream", e);
        }
    }
  }
  
  

}
