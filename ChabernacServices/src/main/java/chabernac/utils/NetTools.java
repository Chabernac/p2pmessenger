package chabernac.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.apache.log4j.Logger;

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
  
}
