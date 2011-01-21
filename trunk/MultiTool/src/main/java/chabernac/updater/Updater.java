package chabernac.updater;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.StringTokenizer;

import chabernac.log.Logger;

public class Updater {

  public static void main(String[] args) {
    Logger.setDebug(true);
    //start the application even if the download has failed.
    
    iApplication theApp = getApplication(args[2]);
    if(theApp != null ){
      theApp.addParameter("host", args[0]);
      theApp.addParameter("port", args[1]);
      theApp.runApplication();
    }

  }

  public static String getVersion(){
    ObjectInputStream theStream = null;
    try {
      theStream = new ObjectInputStream(new FileInputStream(new File("version.bin")));
      return ((Version)theStream.readObject()).getVersion();
    } catch (FileNotFoundException e) {
      Logger.log(Updater.class, "Version object not found", e);
    } catch (IOException e) {
      Logger.log(Updater.class, "Error occured while reading version", e);
    } catch (ClassNotFoundException e) {
      Logger.log(Updater.class, "Error occured while reading version", e);
    } finally {
      if(theStream != null){
        try {
          theStream.close();
        } catch (IOException e) {
          Logger.log(Updater.class, "Could not close stream", e);
        }
      }
    }
    return "0.0.0";
  }
  
  public static iApplication getApplication(String anApplication){
    ObjectInputStream theStream = null;
    try {
      theStream = new ObjectInputStream(new FileInputStream(new File(anApplication + ".bin")));
      return (iApplication)theStream.readObject();
    } catch (FileNotFoundException e) {
      Logger.log(Updater.class, "application object not found", e);
    } catch (IOException e) {
      Logger.log(Updater.class, "Error occured while reading application", e);
    } catch (ClassNotFoundException e) {
      Logger.log(Updater.class, "Error occured while reading application", e);
    } finally {
      if(theStream != null){
        try {
          theStream.close();
        } catch (IOException e) {
          Logger.log(Updater.class, "Could not close stream", e);
        }
      }
    }
    return null;
  }
  
  private static void writeVersion(String aVersion){
    ObjectOutputStream theSteam = null;
    try {
      theSteam = new ObjectOutputStream(new FileOutputStream(new File("version.bin")));
      theSteam.writeObject(new Version(aVersion));
    } catch (FileNotFoundException e) {
      Logger.log(Updater.class, "Version object could not be written", e);
    } catch (IOException e) {
      Logger.log(Updater.class, "Version object could not be written", e);
    } finally {
      if(theSteam != null){
        try {
          theSteam.flush();
          theSteam.close();
        } catch (IOException e) {
          Logger.log(Updater.class, "Could not close stream", e);
        }
      }
    }
  }

  private static int compareVersion(String aVersion1, String aVersion2){
    String[] theVersion1 = explode(aVersion1, ".");
    String[] theVersion2 = explode(aVersion2, ".");
    if(theVersion1.length != theVersion2.length) return 0;

    for(int i=0;i<theVersion1.length;i++){  
      if(Integer.parseInt(theVersion1[i]) > Integer.parseInt(theVersion2[i])) return 1;
      else if(Integer.parseInt(theVersion1[i]) < Integer.parseInt(theVersion2[i])) return -1;
    }
    return 0;
  }

  private static String[] explode(String aString, String aSeperator){
    StringTokenizer theTokenizer = new StringTokenizer(aString, aSeperator);
    String[] theTokens = new String[theTokenizer.countTokens()];
    int i=0;
    while(theTokenizer.hasMoreTokens()){
      theTokens[i++] = theTokenizer.nextToken();
    }
    return theTokens;
  }

}
