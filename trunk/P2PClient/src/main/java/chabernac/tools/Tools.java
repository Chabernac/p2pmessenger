package chabernac.tools;

import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Window;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataSource;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import chabernac.protocol.facade.P2PFacade;
import chabernac.protocol.facade.P2PFacadeException;
import chabernac.protocol.message.MultiPeerMessage;
import chabernac.protocol.userinfo.UserInfo;

public class Tools {
  private static Logger logger = Logger.getLogger(Tools.class);
  

  public static HashMap invertMap(Map aMap){
    HashMap theInvertedMap = new HashMap();
    Set theKeys = aMap.keySet();
    for(Iterator i=theKeys.iterator();i.hasNext();){
      Object theKey = i.next();
      Object theElement = aMap.get(theKey);
      theInvertedMap.put(theElement, theKey);
    }
    return theInvertedMap;
  }





  public static boolean requestFocus(final Window aFrame){
    File theSendFocusExe = new File("SendFocus.exe");
    if(!theSendFocusExe.exists()) return false;
    String theTitle = "";
    if(aFrame instanceof Frame) theTitle = ((Frame)aFrame).getTitle();
    else if(aFrame instanceof Dialog) theTitle = ((Dialog)aFrame).getTitle();
    else return false;
    try {
      String theCMD = "cmd.exe /c " + theSendFocusExe.toString() + " /s " + "\"" + theTitle + "\"";
      logger.debug("SendFocus cmd: " + theCMD);
      Process theProcess = Runtime.getRuntime().exec(theCMD);
      boolean isOk = theProcess.waitFor() == 0;
      if(isOk){
        aFrame.requestFocus();
      }
      return isOk;
    } catch (Exception e) {
      return false;
    }
    
  }

  
  public static int findUnusedLocalPort() throws IOException{
    ServerSocket theSocket  = null;
    try {
      theSocket = new ServerSocket(0);
      return theSocket.getLocalPort();
    } finally{
      if(theSocket != null){
        theSocket.close();
      }
    }
  }

  public static String makeFirstLetterUpper(String aString){
    aString = aString.toUpperCase();
    if(aString.length() > 1) {
      return aString.substring(0,1).toUpperCase() + aString.substring(1).toLowerCase();
    } else {
      return aString;
    }
  }

  public static void initLog4j(DataSource aDataSource) throws FileNotFoundException, IOException{
    Properties theProps = new Properties();
    theProps.load(aDataSource.getInputStream());
    PropertyConfigurator.configure(theProps);
  }



  public static void main(String args[]){
    System.out.println(makeFirstLetterUpper("guy chauliac"));
    /*
    if(Tools.addRun2Registry("messenger", new File("C:\\Projects\\Tasksheduler\\delivery\\versions\\18\\heavy.cmd"))){
      //if(Tools.addRun2Registry("messenger", null)){
      System.out.println("Value: " + getRegistryRunKey("messenger"));
    }
     */
  }

  public static InetAddress getLocalInetAddress() throws SocketException{
    InetAddress theClassAAdress = null;
    InetAddress theClassBAdress = null;
    InetAddress theClassCAdress = null;
    Enumeration theEnum = NetworkInterface.getNetworkInterfaces();
    while(theEnum.hasMoreElements()){
      NetworkInterface theNI = (NetworkInterface)theEnum.nextElement();
      for(Enumeration theIA = theNI.getInetAddresses();theIA.hasMoreElements();){
        InetAddress theAddress = (InetAddress)theIA.nextElement();
        if(theAddress.toString().startsWith("/10")){
          theClassAAdress = theAddress;
        } else if(theAddress.toString().startsWith("/172")){
          theClassBAdress = theAddress;
        } else if(theAddress.toString().startsWith("/169") || theAddress.toString().startsWith("/192")){
          theClassCAdress = theAddress;
        }
      }

    }
    if(theClassAAdress != null) return theClassAAdress;
    if(theClassBAdress != null) return theClassBAdress;
    if(theClassCAdress != null) return theClassCAdress;
    return null;
  }
  
  public static boolean isImage(File aFile){
    String theFileName = aFile.getName();
    String theExtension = theFileName.substring(theFileName.indexOf('.') + 1, theFileName.length());
    if(theExtension.equalsIgnoreCase("JPEG")) return true;
    if(theExtension.equalsIgnoreCase("JPG")) return true;
    if(theExtension.equalsIgnoreCase("GIF")) return true;
    if(theExtension.equalsIgnoreCase("BMP")) return true;
    if(theExtension.equalsIgnoreCase("PNG")) return true;
    if(theExtension.equalsIgnoreCase("TIF")) return true;
    return false;
  }
  
  public static void invokeLaterIfNotEventDispatchingThread(Runnable aRunnable){
    if(!EventQueue.isDispatchThread()){
      SwingUtilities.invokeLater(aRunnable);
    } else {
      aRunnable.run();
    }
  }
  
  public static void invokeLaterAndWaitIfNotEventDispatchingThread(Runnable aRunnable) throws InterruptedException, InvocationTargetException{
    if(!EventQueue.isDispatchThread()){
      SwingUtilities.invokeAndWait( aRunnable);
    } else {
      aRunnable.run();
    }
  }
  
  private static String getShortNameForUser(String aPeerId, P2PFacade aFacade) throws P2PFacadeException{
    Map<String, UserInfo> theUserInfo = aFacade.getUserInfo();
    if(theUserInfo.containsKey( aPeerId )){
      return getShortNameForUser( theUserInfo.get(aPeerId) );
    }
    return aPeerId;
  }
  
  public static String getShortNameForUser(UserInfo aUserInfo){
    String theUserName = aUserInfo.getName();
    if(theUserName == null || theUserName.equals( "" )) theUserName = aUserInfo.getId();
    String[] theUserNameParts = theUserName.split( " " );
    if(theUserNameParts.length >= 2){
      return theUserNameParts[0] + " " + theUserNameParts[1].charAt( 0 );
    } else {
      if(theUserName.length() > 10){
        return theUserName.substring( 0, 10 );
      } else {
        return theUserName;
      }
    }
  }
  
  public static String getEnvelop(P2PFacade aP2Facade, MultiPeerMessage aMessage) throws P2PFacadeException{
    String from = getShortNameForUser(aMessage.getSource(), aP2Facade);

    Set<String> to = aMessage.getDestinations();
    String envelop = "[" + from;
    envelop += "-->";
    for(Iterator< String > i = to.iterator();i.hasNext();){
      envelop += getShortNameForUser( i.next(), aP2Facade );
      if(i.hasNext()) envelop += ",";
    }
    envelop += "]";
    return envelop;
  }
}
