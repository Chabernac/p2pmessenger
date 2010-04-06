package chabernac.tools;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
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
      if(!theInterface.isLoopback() && !theInterface.getDisplayName().toLowerCase().contains( "virtual" )){
        Enumeration<InetAddress> theAddresses = theInterface.getInetAddresses();
        while(theAddresses.hasMoreElements()){
          InetAddress theAddress = theAddresses.nextElement();
          theIpList.add(theAddress.getHostAddress());
        }
      }
    }
    return theIpList;
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
}
