package chabernac.tools;

import java.net.SocketException;

import junit.framework.TestCase;

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
    assertEquals( "127.0.0.1", theInterface.getIp().iterator().next());
  }
}
