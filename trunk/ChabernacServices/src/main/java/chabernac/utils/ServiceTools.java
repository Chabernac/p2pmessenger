package chabernac.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class ServiceTools {
  private static Logger logger = Logger.getLogger(ServiceTools.class);
  private static final char[] HEX = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
  private static final String RUN = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run";

  public static String hex_sha1(String aMessage){
    try {
      return toHexString(encrypt(aMessage.getBytes(), "SHA-1"));
    } catch (NoSuchAlgorithmException e) {
      return "";
    }
  }

  public static String filter(String aMessage){
    char[] theChars = aMessage.toCharArray();
    for(int i=0;i<theChars.length;i++){
      if(!(theChars[i] >= '0' && theChars[i] <= '9' || 
          theChars[i] >= 'a' && theChars[i] <= 'z' ||
          theChars[i] >= 'A' && theChars[i] <= 'Z')){
        theChars[i] = '_';
      }
    }
    return new String(theChars);
  }

  public static String b64_sha1(String aMessage){
    try {
      return new String(Base64.encodeBase64( encrypt(aMessage.getBytes(), "SHA-1")));
    } catch (NoSuchAlgorithmException e) {
      return "";
    }
  }

  public static byte[] encrypt(byte[] aMessage, String anAlgorithm) throws NoSuchAlgorithmException{
    MessageDigest theDigest = MessageDigest.getInstance(anAlgorithm);
    theDigest.update(aMessage);
    return theDigest.digest();
  }

  public static String toHexString(byte[] aByteArray){
    String theResult = "";
    for(int i=0;i<aByteArray.length;i++){
      byte theByte = aByteArray[i];
      byte theWord1 = (byte)((theByte >>> 4) & 0x0F);
      theResult += HEX[theWord1];
      byte theWord2 = (byte)(theByte & 0x0F);
      theResult += HEX[theWord2];
    }
    return theResult;
  }

  public static String toBinaryString(byte aByte){
    String theResult = "";
    for(int i=7;i>=0;i--){
      int theBit = (aByte >>> i) & 0x01;
      theResult += theBit;
    }
    return theResult;
  }

  public static void findFiles(List aFileStore, File aRootDirectory, FileFilter aFileFilter, boolean isSearchSubDirs){
    File[] theFiles = aRootDirectory.listFiles();
    for(int i=0;i<theFiles.length;i++){
      if(theFiles[i].isDirectory()){
        findFiles(aFileStore, theFiles[i], aFileFilter, isSearchSubDirs);
      } else if(aFileFilter.accept(theFiles[i])){
        aFileStore.add(theFiles[i]);
      }
    }
  }
  
  public static File addRun2Startup(File aFile) throws IOException{
    File theLocation = new File("C:\\Documents and Settings\\" + System.getProperty( "user.name" ) + "\\Start Menu\\Programs\\Startup\\" + aFile.getName()+ ".cmd");
    
    PrintWriter theWriter = null;
    try{
     theWriter = new PrintWriter(new FileWriter( theLocation ));
     theWriter.println(aFile.getAbsolutePath());
     theWriter.flush();
    }finally{
      if(theWriter != null){
        theWriter.close();
      }
    }
    return theLocation;
  }
  
  public static boolean removeRunAtStartup(File aFile){
    File theLocation = new File("C:\\Documents and Settings\\" + System.getProperty( "user.name" ) + "\\Start Menu\\Programs\\Startup\\" + aFile.getName()+ ".cmd");
    if(theLocation.exists()){
      return theLocation.delete();
    }
    return true;
  }

  public static boolean addRun2Registry(String aKey, File aFile){
    //if(!aFile.exists()) return false;
    File theRegFile = new File("temp.reg");
    PrintWriter theWriter = null;

    try {
      theWriter = new PrintWriter(new FileOutputStream(theRegFile));
      theWriter.println("REGEDIT4");
      theWriter.println();
      theWriter.println("[" + RUN + "]");
      theWriter.print("\"" + aKey + "\"=");
      theWriter.println(aFile != null ? "\"" + doubleEscape(aFile.getAbsolutePath()) + "\"" : "-");
      theWriter.flush();
    } catch (FileNotFoundException e) {
      logger.error("Could not create reg file", e);
    } finally{
      if(theWriter != null){
        theWriter.close();
      }
    }

    try{
      if(theRegFile.exists()) {
        Process theProcess = Runtime.getRuntime().exec("regedit /s temp.reg");
        boolean success = (theProcess.waitFor() == 0);
        theRegFile.delete();
        return success;
      }
    } catch (IOException e) {
      logger.error("Could not import reg file in registry", e);
    } catch(InterruptedException e){
      logger.error("The import of the reg file was interrupted", e);
    }
    return false;
  }
  
  private static String cleanRegistryString(String aString){
    aString = aString.replace( new String(new byte[]{0}), "" );
    aString = aString.replace( "\"", "");
    return aString;
  }

  public static String getRegistryRunKey(String aKey){
    File theTempRegFile = new File("temp.reg");
    BufferedReader theReader = null;
    try {
      Process theProcess = Runtime.getRuntime().exec("regedit /e " + theTempRegFile.getName() + " " + RUN);
      int returnValue = theProcess.waitFor();
      if(returnValue == 0){
        theReader = new BufferedReader(new FileReader(theTempRegFile));
        String theRegLine = null;
        while((theRegLine = theReader.readLine()) != null ){
          StringTokenizer theTokenizer = new StringTokenizer(theRegLine, "=");
          if(theTokenizer.countTokens() == 2){
            String theKey = cleanRegistryString( theTokenizer.nextToken() );
            if(theKey.equalsIgnoreCase(aKey)){
              return cleanRegistryString( theTokenizer.nextToken() );
            }
          }
        }
      }
    } catch (IOException e) {
      logger.error("Could not extract registry folder", e);
    } catch (InterruptedException e) {
      logger.error("The registry extract was interrupted", e);
    } finally {
      if(theReader != null){
        try {
          theReader.close();
        } catch (IOException e) {
          logger.error("Could not close inputstream", e);
        }
      }
      if(theTempRegFile.exists()){
        theTempRegFile.delete();
      }
    }
    return null;
  }

  public static String doubleEscape(String aString){
    StringTokenizer theTokenizer = new StringTokenizer(aString, "\\");
    String theNewString = theTokenizer.nextToken();
    while(theTokenizer.hasMoreElements()){
      theNewString +=  "\\\\" + theTokenizer.nextToken();
    }
    return theNewString;
  }

  public static void configureLog4j(File aFile) throws FileNotFoundException, IOException{
    System.out.println("Configuring log4j");
    Properties theProps = new Properties();
    theProps.load(new FileInputStream(aFile));
    PropertyConfigurator.configure(theProps);
  }

  public static Class[] getClasses(String pckgname)
  throws ClassNotFoundException {
    ArrayList classes = new ArrayList();
//  Get a File object for the package
    File directory = null;
    try {
      ClassLoader cld = Thread.currentThread().getContextClassLoader();
      if (cld == null) {
        throw new ClassNotFoundException("Can't get class loader.");
      }
      String path = '/' + pckgname.replace('.', '/');
      URL resource = cld.getResource(path);
      if (resource == null) {
        throw new ClassNotFoundException("No resource for " + path);
      }
      directory = new File(resource.getFile());
    } catch (NullPointerException x) {
      throw new ClassNotFoundException(pckgname + " (" + directory
          + ") does not appear to be a valid package");
    }
    if (directory.exists()) {
      // Get the list of the files contained in the package
      String[] files = directory.list();
      for (int i = 0; i < files.length; i++) {
        // we are only interested in .class files
        if (files[i].endsWith(".class")) {
          // removes the .class extension
          classes.add(Class.forName(pckgname + '.'
              + files[i].substring(0, files[i].length() - 6)));
        }
      }
    } else {
      throw new ClassNotFoundException(pckgname
          + " does not appear to be a valid package");
    }
    Class[] classesA = new Class[classes.size()];
    classes.toArray(classesA);
    return classesA;
  }
  
  public static Class getWrapperClass(Class aClass){
    if(!aClass.isPrimitive()){
      return aClass;
    }
    if(aClass == Byte.TYPE) return Byte.class;
    if(aClass == Short.TYPE) return Short.class;
    if(aClass == Integer.TYPE) return Integer.class;
    if(aClass == Long.TYPE) return Long.class;
    if(aClass == Float.TYPE) return Float.class;
    if(aClass == Double.TYPE) return Double.class;
    if(aClass == Character.TYPE) return Character.class;
    
    return aClass;
  }

  public static void main(String args[]){
    System.out.println(filter(b64_sha1(System.getProperty("user.name"))));
  }
}
