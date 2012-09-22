package chabernac.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;

import chabernac.tools.SimpleNetworkInterface;

public class NetTools {
  private static final Logger LOGGER = Logger.getLogger(NetTools.class);
  
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
  
  public static InetAddress getLocalInetAddress() throws SocketException{
		InetAddress theClassAAdress = null;
		InetAddress theClassBAdress = null;
		InetAddress theClassCAdress = null;
		InetAddress theOtherAddress = null;

		Enumeration theEnum = NetworkInterface.getNetworkInterfaces();
		while(theEnum.hasMoreElements()){
			NetworkInterface theNI = (NetworkInterface)theEnum.nextElement();
			for(Enumeration theIA = theNI.getInetAddresses();theIA.hasMoreElements();){
				InetAddress theAddress = (InetAddress)theIA.nextElement();
				if(theAddress.toString().startsWith("/10")){
					theClassAAdress = theAddress;
				} else if(theAddress.toString().startsWith("/172")){
					theClassBAdress = theAddress;
				} else if(theAddress.toString().startsWith("/169")){
					theClassCAdress = theAddress;
				} else {
					theOtherAddress = theAddress;
				}
			}
		}

		if(theClassAAdress != null) return theClassAAdress;
		if(theClassBAdress != null) return theClassBAdress;
		if(theClassCAdress != null) return theClassCAdress;
		if(theOtherAddress != null) return theOtherAddress;
		throw new SocketException("No local network interfaces found");
	}
  
  public static boolean isLocalAddress(InetAddress anAddress){
  	if(anAddress.isAnyLocalAddress()) return true;
  	if(anAddress.isLoopbackAddress()) return true;
  	return false;
  }
  
  public static URL resolveURL(URL anUrL){
    try{
      InetAddress theInetAddress = InetAddress.getByName(anUrL.getHost());
      return  new URL(new URL("http://" + theInetAddress.getHostAddress()), anUrL.getPath());
    }catch(Exception e){
      LOGGER.error("Unable to resolve url '"  + anUrL  + "' using url as it is");
      return anUrL;
    }
  }
  
  public static ServerSocket openServerSocket(int aFromPort) throws IOException{
    while(aFromPort < 65000){
      try {
        return new ServerSocket(aFromPort++);
      } catch (IOException e) {
      }
    }
    throw new IOException("Server socket could not be openend");
  }
  
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
        
        for(InterfaceAddress theAddress : theInterface.getInterfaceAddresses()){
          theIpAddresses.add(theAddress.getAddress().getHostAddress() + "/" + theAddress.getNetworkPrefixLength());
        }
        
        if(theIpAddresses.size() > 0){
          theIpList.add( new SimpleNetworkInterface(theInterface.getName(), theInterface.isLoopback(), theInterface.getHardwareAddress(), theIpAddresses.toArray(new String[]{})) );
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
        
        for(InterfaceAddress theAddress : theInterface.getInterfaceAddresses()){
          theIpAddresses.add(theAddress.getAddress().getHostAddress() + "/" + theAddress.getNetworkPrefixLength());
        }
        return SimpleNetworkInterface.createForLoopBack( theInterface.getDisplayName() );
      }
    }
    return null;
  }
  
  public static SimpleNetworkInterface getNetworkInterfaceForLocalIP(String aLocalIp) throws SocketException{
    Enumeration<NetworkInterface> theInterfaces = NetworkInterface.getNetworkInterfaces();
    while(theInterfaces.hasMoreElements()){
      NetworkInterface theInterface = theInterfaces.nextElement();
        for(InterfaceAddress theAddress : theInterface.getInterfaceAddresses()){
          if(aLocalIp.equalsIgnoreCase( theAddress.getAddress().getHostAddress() )){
            return new SimpleNetworkInterface(theInterface.getDisplayName(), theInterface.isLoopback(), theInterface.getHardwareAddress(), aLocalIp);
          }
      }
    }
    return null;
  }
  
  
  private static boolean isCandidate(NetworkInterface anInterface) throws SocketException{
    if(anInterface.isLoopback()) return false;
    if(anInterface.getDisplayName() == null) return true;
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
