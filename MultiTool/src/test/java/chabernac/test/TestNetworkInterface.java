package chabernac.test;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import chabernac.util.Tools;


public class TestNetworkInterface {
  public static void main(String args[]){
    
    try{
      System.out.println(Tools.getLocalInetAddress());
     Enumeration theEnum = NetworkInterface.getNetworkInterfaces();
     while(theEnum.hasMoreElements()){
       NetworkInterface theNI = (NetworkInterface)theEnum.nextElement();
       for(Enumeration theIA = theNI.getInetAddresses();theIA.hasMoreElements();){
         InetAddress theAddress = (InetAddress)theIA.nextElement();
         System.out.println(theNI.getName() + ": " + theNI.getDisplayName() + ": " + theAddress);
       }
       
     }
    }catch(Exception e){
      e.printStackTrace();
    }
  }

}
