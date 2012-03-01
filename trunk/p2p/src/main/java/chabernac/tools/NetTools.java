package chabernac.tools;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class NetTools {
  public static List<String> getLocalExposedIpAddresses() throws SocketException{
    List<String> theIpList = new ArrayList<String>();
    Enumeration<NetworkInterface> theInterfaces = NetworkInterface.getNetworkInterfaces();
    while(theInterfaces.hasMoreElements()){
      NetworkInterface theInterface = theInterfaces.nextElement();
//      print(theInterface);
      if(isCandidate( theInterface )){
        Enumeration<InetAddress> theAddresses = theInterface.getInetAddresses();
        while(theAddresses.hasMoreElements()){
          InetAddress theAddress = theAddresses.nextElement();
          theIpList.add(theAddress.getHostAddress());
        }
      }
    }
    return theIpList;
  }
  
  public static List<SimpleNetworkInterface> getLocalExposedInterfaces() throws SocketException{
    List<SimpleNetworkInterface> theIpList = new ArrayList<SimpleNetworkInterface>();
    Enumeration<NetworkInterface> theInterfaces = NetworkInterface.getNetworkInterfaces();
    while(theInterfaces.hasMoreElements()){
      NetworkInterface theInterface = theInterfaces.nextElement();
      if(isCandidate( theInterface )){
        List<String> theIpAddresses = new ArrayList< String >();
        Enumeration<InetAddress> theAddresses = theInterface.getInetAddresses();
        while(theAddresses.hasMoreElements()){
          InetAddress theAddress = theAddresses.nextElement();
          theIpAddresses.add(theAddress.getHostAddress());
        }
        if(theIpAddresses.size() > 0){
          theIpList.add( new SimpleNetworkInterface(theIpAddresses, theInterface.getHardwareAddress()) );
        }
      }
    }
    return theIpList;
  }
  
  public static SimpleNetworkInterface getLoopBackInterface() throws SocketException{
    Enumeration<NetworkInterface> theInterfaces = NetworkInterface.getNetworkInterfaces();
    while(theInterfaces.hasMoreElements()){
      NetworkInterface theInterface = theInterfaces.nextElement();
      if(theInterface.isLoopback()) {
        List<String> theIpAddresses = new ArrayList< String >();
        Enumeration<InetAddress> theAddresses = theInterface.getInetAddresses();
        while(theAddresses.hasMoreElements()){
          InetAddress theAddress = theAddresses.nextElement();
          theIpAddresses.add(theAddress.getHostAddress());
        }
        return  new SimpleNetworkInterface(theIpAddresses, theInterface.getHardwareAddress());
      }
    }
    return null;
  }
  
  
  private static boolean isCandidate(NetworkInterface anInterface) throws SocketException{
    if(anInterface.isLoopback()) return false;
    if(anInterface.getDisplayName().toLowerCase().contains( "check point" )) return true;
    if(anInterface.getDisplayName().toLowerCase().contains( "virtual" )) return false;
    return true;
  }
  
  private static void print(NetworkInterface anInterface){
    Enumeration< InetAddress > theAddresses = anInterface.getInetAddresses();
    while(theAddresses.hasMoreElements()){
      System.out.println(theAddresses.nextElement());
    }
  }
}
