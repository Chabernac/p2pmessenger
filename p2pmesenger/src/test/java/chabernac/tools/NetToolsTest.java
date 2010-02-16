package chabernac.tools;

import java.net.SocketException;

import junit.framework.TestCase;

public class NetToolsTest extends TestCase{
  public void testGetLocalExposedAddresses() throws SocketException{
    for(String theHost : NetTools.getLocalExposedIpAddresses()){
      System.out.println(theHost);
    }
  }
}
