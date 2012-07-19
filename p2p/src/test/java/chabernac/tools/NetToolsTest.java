package chabernac.tools;

import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import junit.framework.TestCase;
import chabernac.io.SimpleNetworkInterface;
import chabernac.utils.NetTools;

public class NetToolsTest extends TestCase{
  public void testGetLocalExposedAddresses() throws SocketException{
    for(String theHost : NetTools.getLocalExposedIpAddresses()){
      System.out.println(theHost);
    }
  }

  public void testGetLocalExposedInterfaces() throws SocketException{
    for(SimpleNetworkInterface theHost : NetTools.getLocalExposedInterfaces()){
      System.out.println(theHost);
    }
  }

  public void testNetToolsPerformanceTest() throws SocketException{
    long t1 = System.currentTimeMillis();
    int times = 500;
    for(int i=0;i<times;i++){
      NetTools.getLocalExposedInterfaces();
    }
    float theSeconds = (System.currentTimeMillis() - t1) / 1000F;
    float theTimesPerSecond = ((float)times) / theSeconds;
    int theLimit = 20;
    assertTrue( "Number of local interfaces lookup per second '" + theTimesPerSecond + "' < '" + theLimit + "'", theTimesPerSecond > theLimit );
  }

  public void testGetLoopbackInterface() throws SocketException{
    SimpleNetworkInterface theInterface = NetTools.getLoopBackInterface();
    assertNotNull( theInterface );
    assertEquals( "127.0.0.1/8", theInterface.getIp().get(0));
  }
  
  public void testGetInterfaceForIp() throws SocketException{
    SimpleNetworkInterface theInterface = NetTools.getNetworkInterfaceForLocalIP( "127.0.0.1" );
    System.out.print( theInterface.getId() );
    assertNotNull( theInterface );
    assertNotNull( theInterface.getId() );
  }

  public void testPrintAllInterfacesAndIP() throws SocketException{
    Enumeration<NetworkInterface> theInterfaces = NetworkInterface.getNetworkInterfaces();
    while(theInterfaces.hasMoreElements()){
      NetworkInterface theInterface = theInterfaces.nextElement();
      for(InterfaceAddress theAddress : theInterface.getInterfaceAddresses()){
        System.out.println(theInterface.getDisplayName() +  " mac: " + theInterface.getHardwareAddress() +  "(" + theAddress.getAddress().getHostAddress() + "/" + theAddress.getNetworkPrefixLength() + ")");
      }
    }
  }
}
