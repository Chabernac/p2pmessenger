package chabernac.net;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;

public class ListNetsEx
{
    public static void main(String args[]) 
      throws SocketException 
    {
      Enumeration<NetworkInterface> theInterfaces = NetworkInterface.getNetworkInterfaces();
      while(theInterfaces.hasMoreElements())
            displayInterfaceInformation(theInterfaces.nextElement());
    }

    static void displayInterfaceInformation(NetworkInterface netint) 
      throws SocketException 
    {
//        System.out.println("Display name: " 
//           + netint.getDisplayName());
//        System.out.println("Hardware address: " 
//           + Arrays.toString(netint.getHardwareAddress()));
        Enumeration<InetAddress> theAddresses = netint.getInetAddresses();
        while(theAddresses.hasMoreElements()){
          System.out.println("IP: " + theAddresses.nextElement().toString() + " " + netint.isLoopback());
        }
    }
}  
