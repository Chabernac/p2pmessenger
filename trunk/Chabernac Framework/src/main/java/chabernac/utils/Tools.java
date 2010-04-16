package chabernac.utils;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;

import sun.misc.BASE64Encoder;

import chabernac.io.AbstractResource;
import chabernac.io.JARResource;
import chabernac.io.iResource;
import chabernac.queue.QueueInterface;

public class Tools
{
  private static final String RUN = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run";

  private static Logger logger = Logger.getLogger(chabernac.utils.Tools.class);
  private static final char HEX[] = {
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
    'a', 'b', 'c', 'd', 'e', 'f'
  };

  private static Toolkit myToolkit = null;

  static{
    myToolkit = Toolkit.getDefaultToolkit();
  }

  public static boolean stringContainsValueOfVector(String aString, Vector aVector, char[] aSuffix, boolean suffix)
  {
    int theIndex = -1;
    String theWord = null;
    for(int i=0;i<aVector.size();i++)
    {
      theWord = ((String)aVector.elementAt(i)).toUpperCase();
      while((theIndex = aString.toUpperCase().indexOf(theWord,theIndex + 1))!=-1)
      {
        //theIndex points to the last character
        if(!suffix){return true;}
        if(theIndex + theWord.length() == aString.length()){return true;}
        for(int j=0;j<aSuffix.length;j++)
        {
          if(aString.charAt(theIndex + theWord.length()) == aSuffix[j])
          {
            return true;
          }
        }
      }
    }
    return false;
  }

  public static boolean stringEndsWidthValueOfVector(String aString, Vector aVector)
  {
    for(int i=0;i<aVector.size();i++)
    {
      if(aString.endsWith((String)aVector.elementAt(i)))
      {
        return true;
      }
    }
    return false;
  }

  public static Object growArray(Object a, int grow)
  {
    Class cl = a.getClass();
    if(!cl.isArray()) return null;;
    Class componentType = a.getClass().getComponentType();
    int length = Array.getLength(a);
    int newLength = length + grow;
    Object newArray = Array.newInstance(componentType,newLength);
    System.arraycopy(a,0,newArray,0,length);
    return newArray;
  }

  public static Object trimArray(Object a, int size)
  {
    Class cl = a.getClass();
    if(!cl.isArray()) return null;;
    Class componentType = a.getClass().getComponentType();
    Object newArray = Array.newInstance(componentType,size);
    System.arraycopy(a,0,newArray,0,size);
    return newArray;
  }

  public static void checkMemory()
  {
    Runtime runtime =  Runtime.getRuntime();
    Debug.log(Tools.class,runtime.freeMemory() + " memory free of "  + runtime.totalMemory()  + " total memory");
  }

  public static int byteValue(byte aByte, byte start, byte offset)
  {

    byte value = 0;
    for(byte i=0;i<=offset;i++)
    {
      value = (byte)(value | (byte)(aByte & (byte)Math.pow((byte)2,start + i)));
    }
    Debug.log(Tools.class,"Startbit: " + start + " offset: " + offset  + " value: " + (byte)(value / (byte)Math.pow((byte)2,start)) + " byte: " + aByte);
    int result = (int)value;
    if(result < 0){result = result + 256;}
    return result / (int)Math.pow(2,start);
    //return (byte)(value / (byte)Math.pow((byte)2,start));


    /*
		int value = 0;
		for(byte i=0;i<=offset;i++)
		{
			value = (value | (aByte & Math.pow(2,start + i)));
			Debug.log(Tools.class,"Startbit: " + start + " offset: " + offset  + " value: " + value + " byte: " + aByte);
		}
		return (value / Math.pow(2,start));
     */
  }

  public static void printBytes(byte[] theBytes)
  {
    for(int i=0;i<theBytes.length;i++)
    {
      Debug.log(Tools.class,"Byte " + i + ": " + Byte.toString(theBytes[i]));
    }
  }

  public static boolean readBytes(InputStream inputStream, byte[] store, int currentPosition) throws IOException
  {
    while(currentPosition < store.length && inputStream.available() > 0)
    {
      currentPosition = currentPosition + inputStream.read(store, currentPosition, store.length - currentPosition);
    }
    if(currentPosition >= store.length){return true;}
    else{return false;}
  }

  public static int compare(Object a, Object b, Method m)
  {
    return 0;
  }

  public static void setupDebugging(String args[])
  {
    if(args != null && args.length > 0)
    {
      if(args[0].toUpperCase().equals("DEBUG"))
      {
        Debug.setDebug(true);
      }
      if(args.length > 1)
      {
        Class[] debugClasses = new Class[args.length - 1];
        for(int i=1;i<args.length;i++)
        {
          try
          {
            debugClasses[i-1] = Class.forName(args[i]);
            Debug.log(Tools.class,"Debugging on for: " + debugClasses[i-1].toString());
          }catch(Exception e){Debug.log(Tools.class,"Don't know this class!!",e);}
          //debugClasses[i-1] = chabernac.chabygrabber.spider.UrlSpider.class;
          //debugClasses[1] = chabernac.chabygrabber.multidownloader.DownloadUrl.class;
        }
        Debug.setDebugClasses(debugClasses);
      }
    }
    else
    {
      Debug.setDebug(false);
    }
  }

  public static void setSystemOut(String out) throws FileNotFoundException
  {
    System.setOut(new PrintStream(new FileOutputStream(out)));
  }

  public static void setSystemErr(String err) throws FileNotFoundException
  {
    System.setErr(new PrintStream(new FileOutputStream(err)));
  }

  public static void setSystemStreams(String out, String err) throws FileNotFoundException
  {
    setSystemOut(out);
    setSystemErr(err);
  }

  public static void closeSystemStreams()
  {
    System.out.flush();
    System.out.close();
    System.err.flush();
    System.err.close();
  }

  public static Vector convertArrayToVector(Object[] array)
  {
    Vector theVector = new Vector(array.length);
    for(int i=0;i<array.length;i++)
    {
      theVector.addElement(array[i]);
    }
    return theVector;
  }

  public static Image[][] splitImage(Image image, int rows, int columns, int spacingX, int spacingY){
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    if (rows <= 0 || columns <= 0 || spacingX < 0 || spacingY < 0){
      throw new IllegalArgumentException();
    }
    if (image == null || toolkit == null) {
      throw new NullPointerException();
    }
    waitFor(image);
    ImageProducer src = image.getSource();
    //Image[] result = new Image[rows * columns];
    Image[][] result = new Image[columns][rows];

    int W = image.getWidth(null);
    int H = image.getHeight(null);
    int dx = (W - spacingX) / columns;
    int dy = (H - spacingY) / rows;
    W = (dx * columns) + spacingX;
    H = (dy * rows) + spacingY;
    int w = dx - spacingX;
    int h = dy - spacingY;

    int i = 0;
    int xLoc = 0;
    int yLoc = 0;
    for (int y = 0; y < H; y += dy) {
      for (int x = 0; x < W; x += dx) {
        result[xLoc][yLoc] = toolkit.createImage(new FilteredImageSource(src, new CropImageFilter(x, y, w, h)));
        xLoc++;
      }
      xLoc = 0;
      yLoc++;
    }
    return result;
  }

  public static Image loadImage(String aString) throws Exception{
    Image theImage = null;
    if(aString.toUpperCase().startsWith("HTTP://")){
      theImage = myToolkit.createImage(new URL(aString));
    } else {
      theImage = myToolkit.createImage(aString);
    }
    waitFor(theImage);
    return theImage;
  }

  public static void waitFor(Image image) {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    ImageObserver observer = new ImageObserver() {
      public boolean imageUpdate(Image img, int flags, int x, int y, int w, int h) {
        boolean complete = ((flags & (ALLBITS | FRAMEBITS | ABORT)) != 0);
        if (complete) {
          synchronized (this) {
            notifyAll();
          }
        }
        return !complete;
      }
    };

    try {
      synchronized (observer) {
        while (!toolkit.prepareImage(image, -1, -1, observer)) {
          observer.wait();
        }
      }
    } catch (InterruptedException e) {
      System.err.println("com.starfarer.util.Images.waitFor(Image,Toolkit) caught an exception:");
      e.printStackTrace(System.err);
    }
  }

  public static void cloneArray(Object source[][], Object target[][]){
    for(int i=0;i<source.length;i++){
      for(int j=0;j<source[i].length;j++){
        target[i][j] = source[i][j];
      }
    }
  }

  public static Vector readTemplate(File aFile) throws Exception
  {
    Vector theTemplate = new Vector();
    BufferedReader reader = null;
    String line = null;
    StringTokenizer tokenizer = null;
    try
    {
      reader = new BufferedReader(new FileReader(aFile));
      while((line = reader.readLine())!=null)
      {
        tokenizer = new StringTokenizer(line,"$");
        while(tokenizer.hasMoreTokens())
        {
          theTemplate.addElement(tokenizer.nextToken());
        }
        theTemplate.add("\n");
      }
    }finally
    {
      if(reader!=null)
      {
        reader.close();
      }
    }
    return theTemplate;
  }

  public static File insertPrefix(File aFile, String aPrefix){
    if(aPrefix.equals("")){
      return aFile;
    }
    String theFile = aFile.toString();
    return new File(theFile.substring(0,theFile.lastIndexOf("\\")) + "\\" + aPrefix + theFile.substring(theFile.lastIndexOf("\\") + 1, theFile.length()));
  }

  public static void putVectorOnQueue(QueueInterface aQueue, Vector aVector) throws Exception{
    for(int i=0;i<aVector.size();i++){
      aQueue.put(aVector.elementAt(i));
    }
  }

  public static String replace(String aString, String aSearchString, String aNewString){
    StringBuffer theStringBuffer = new StringBuffer();
    int start = 0;
    int end = 0;
    while((end = aString.indexOf(aSearchString,start)) != -1){
      theStringBuffer.append(aString.substring(start,end));
      theStringBuffer.append(aNewString);
      start = end + aSearchString.length();
    }
    theStringBuffer.append(aString.substring(start, aString.length()));
    return theStringBuffer.toString();
  }

  public static String addPrefix(String aString, String aPrefix, int length){
    StringBuffer theBuffer = new StringBuffer(aString);
    while(theBuffer.length() < length){
      theBuffer.insert(0, aPrefix);
    }
    return theBuffer.toString();
  }

  public static String addSuffix(String aString, String aSuffix, int length){
    StringBuffer theBuffer = new StringBuffer(aString);
    while(theBuffer.length() < length){
      theBuffer.append(aSuffix);
    }
    return theBuffer.toString();
  }

  public static boolean isNumber(String aString){
    return isNumber(aString.getBytes());
  }

  public static boolean isNumber(byte[] bytes){
    return bytesBetween(bytes, (byte)'0',(byte)'9');
  }

  public static boolean bytesBetween(byte[] bytes, byte aStartByte, byte aEndByte){
    for(int i=0;i<bytes.length;i++){
      if(bytes[i] < aStartByte || bytes[i] > aEndByte) return false;
    }
    return true;
  }

  public static String hex_sha1(String aMessage)
  {
    try{
      return toHexString(encrypt(aMessage.getBytes(), "SHA-1"));
    }catch(NoSuchAlgorithmException e){
      return "";
    }
  }

  public static String filter(String aMessage)
  {
    char theChars[] = aMessage.toCharArray();
    for(int i = 0; i < theChars.length; i++)
      if((theChars[i] < '0' || theChars[i] > '9') && (theChars[i] < 'a' || theChars[i] > 'z') && (theChars[i] < 'A' || theChars[i] > 'Z'))
        theChars[i] = '_';

    return new String(theChars);
  }

  public static String b64_sha1(String aMessage)
  {
    BASE64Encoder theEncoder = new BASE64Encoder();
    try{
      return theEncoder.encode(encrypt(aMessage.getBytes(), "SHA-1"));
    }catch(NoSuchAlgorithmException e){
      return "";
    }
  }

  public static byte[] encrypt(byte aMessage[], String anAlgorithm)
  throws NoSuchAlgorithmException
  {
    MessageDigest theDigest = MessageDigest.getInstance(anAlgorithm);
    theDigest.update(aMessage);
    return theDigest.digest();
  }

  public static String toHexString(byte aByteArray[])
  {
    String theResult = "";
    for(int i = 0; i < aByteArray.length; i++)
    {
      byte theByte = aByteArray[i];
      byte theWord1 = (byte)(theByte >>> 4 & 0xf);
      theResult = theResult + HEX[theWord1];
      byte theWord2 = (byte)(theByte & 0xf);
      theResult = theResult + HEX[theWord2];
    }

    return theResult;
  }

  public static String toBinaryString(byte aByte)
  {
    String theResult = "";
    for(int i = 7; i >= 0; i--)
    {
      int theBit = aByte >>> i & 1;
      theResult = theResult + theBit;
    }

    return theResult;
  }

  public static void findFiles(List aFileStore, File aRootDirectory, FileFilter aFileFilter, boolean isSearchSubDirs)
  {
    File theFiles[] = aRootDirectory.listFiles();
    for(int i = 0; i < theFiles.length; i++)
      if(theFiles[i].isDirectory()){
        findFiles(aFileStore, theFiles[i], aFileFilter, isSearchSubDirs);
      } else if(aFileFilter.accept(theFiles[i])){
        aFileStore.add(theFiles[i]);
      }

  }

  public static boolean addRun2Registry(String aKey, File aFile){
    File theRegFile  = new File("temp.reg");
    PrintWriter theWriter = null;
    try{
      theWriter = new PrintWriter(new FileOutputStream(theRegFile));
      //theWriter.println("REGEDIT4");
      theWriter.println("Windows Registry Editor Version 5.00");
      theWriter.println();
      theWriter.println("[" + RUN + "]");
      theWriter.print("\"" + aKey + "\"=");
      theWriter.println(aFile == null ? "-" : "\"" + doubleEscape(aFile.getAbsolutePath()) + "\"");
      theWriter.flush();
    } catch(Exception e){
      logger.error("Could not update registry", e);
      return false;
    } finally {
      if(theWriter != null){
        theWriter.close();
      }
    }

    try{
      if(theRegFile.exists()){
        Process theProcess = Runtime.getRuntime().exec("regedit /s temp.reg");
        int theResult = theProcess.waitFor();
        return theResult == 0;
      }
      return false;
    } catch(Exception e){
      logger.error("Could not update registry", e);
      return false;
    } finally {
      if(theRegFile.exists()){
        theRegFile.delete();
      }

    }
  }

  public static String getRegistryRunKey(String aKey) {
    File theTempRegFile = new File("temp.reg");

    BufferedReader theReader = null;
    try{
      Process theProcess = Runtime.getRuntime().exec("regedit /e " + theTempRegFile.getName() + " " + RUN);
      if(theProcess.waitFor() != 0){
        return null;
      }

      theReader = new BufferedReader(new InputStreamReader(new FileInputStream(theTempRegFile), "UTF-16"));

      String theRegLine = null;

      while((theRegLine = theReader.readLine()) != null){
        //System.out.println(theRegLine);
        StringTokenizer theTokenizer = new StringTokenizer(theRegLine, "=");
        if(theTokenizer.countTokens() == 2){
          String theKey = theTokenizer.nextToken();
          if(theKey.replace('"', ' ').trim().equalsIgnoreCase(aKey)){
            return theTokenizer.nextToken();
          }
        }

      }
      return null;
    }catch(Exception e){
      logger.error("An error occured while extracting registry key" ,e);
      return "";
    } finally {
      if(theTempRegFile.exists()){
        theTempRegFile.delete();
      }
      if(theReader != null){
        try {
          theReader.close();
        } catch (IOException e) {
          logger.error("Could not close reader", e);
        }
      }

    }
  }

  public static String doubleEscape(String aString)
  {
    StringTokenizer theTokenizer = new StringTokenizer(aString, "\\");
    String theNewString;
    for(theNewString = theTokenizer.nextToken(); theTokenizer.hasMoreElements(); theNewString = theNewString + "\\\\" + theTokenizer.nextToken());
    return theNewString;
  }

  public static BufferedImage makeTransparent(BufferedImage anImage, Color[] colors){
    BufferedImage theNewImage = new BufferedImage(anImage.getWidth(), anImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
    for(int x=0;x<anImage.getWidth();x++){
      for(int y=0;y<anImage.getHeight();y++){
        int theRGB = anImage.getRGB(x, y);
        for(int i=0;i<colors.length;i++){
          if((theRGB & 0x00FFFFFF) == (colors[i].getRGB() & 0x00FFFFFF)){
            theRGB &= 0x00FFFFFF;
          } 
          theNewImage.setRGB(x, y, theRGB);
        }
      }
    }
    return theNewImage;
  }
  
  public static Class[] getPackageClassesOfType(Package aPackage, Class aType){
    Class[] theClasses = getPackageClasses(aPackage);
    ArrayList theList = new ArrayList();
    for(int i=0;i<theClasses.length;i++){
      if(aType.isAssignableFrom(theClasses[i])){
        theList.add(theClasses[i]);
      }
    }
    Class[] theSubList = new Class[theList.size()];
    System.arraycopy(theList.toArray(), 0, theSubList, 0, theSubList.length);
    return theSubList;
  }

  public static Class[] getPackageClasses(Package aPackage){
    ArrayList theClasses = new ArrayList();
    JARResource theResource = new JARResource(aPackage.getName().replace('.', '/'));
    if(theResource.exists()){
      File theDir = theResource.getFile();
      if(theDir.exists()){
        String[] theFiles = theDir.list();
        for(int i=0;i<theFiles.length;i++){
          if(theFiles[i].endsWith(".class")){
            String theClassName = theResource.getLocation() + "/" + theFiles[i].substring(0, theFiles[i].length() - 6);
            try{
              Class theClass = Class.forName(theClassName.replace('/', '.'));
              theClasses.add(theClass);
            }catch(ClassNotFoundException e){
              logger.error("An error occured while searching class: " + theClassName, e);
            }
          }
        }
      }
    }
    Class[] theClassArray = new Class[theClasses.size()];
    System.arraycopy(theClasses.toArray(), 0, theClassArray, 0, theClassArray.length);
    return theClassArray;
  }


}


