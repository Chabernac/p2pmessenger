/**
 * Copyright (c) 2012 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

public class IPAddressTest extends TestCase {
  
  public void testClasses() throws InvalidIpAddressException{
    assertEquals( IPAddress.IPClass.C,  new IPAddress("192.0.0.1").getIPClass());
    assertEquals( IPAddress.IPClass.C,  new IPAddress("223.255.255.255").getIPClass());
    assertEquals( IPAddress.IPClass.B,  new IPAddress("128.0.0.1").getIPClass());
    assertEquals( IPAddress.IPClass.B,  new IPAddress("191.255.255.255").getIPClass());
    assertEquals( IPAddress.IPClass.A,  new IPAddress("0.0.0.1").getIPClass());
    assertEquals( IPAddress.IPClass.A,  new IPAddress("127.255.255.255").getIPClass());
  }
  
  public void testPublicPrivate() throws InvalidIpAddressException{
    assertEquals( false, new IPAddress("192.0.0.1").isPrivate());
    assertEquals( false, new IPAddress("223.255.255.255").isPrivate());
    assertEquals( true,  new IPAddress("192.168.0.0").isPrivate());
    assertEquals( true,  new IPAddress("192.168.255.255").isPrivate());

    assertEquals( false,  new IPAddress("128.0.0.1").isPrivate());
    assertEquals( false, new IPAddress("191.255.255.255").isPrivate());
    assertEquals( true,  new IPAddress("172.16.0.0").isPrivate());
    assertEquals( true,  new IPAddress("172.31.255.255").isPrivate());
    
    assertEquals( false, new IPAddress("0.0.0.1").isPrivate());
    assertEquals( false, new IPAddress("127.255.255.255").isPrivate());
    assertEquals( true, new IPAddress("10.0.0.0").isPrivate());
    assertEquals( true, new IPAddress("10.255.255.255").isPrivate());
  }
  
  public void testBinaryRepresenation() throws InvalidIpAddressException{
    assertEquals( "10101100000100001111111000000001", new IPAddress("172.16.254.1").getBitRepresentation() );
  }
  
  public void testNetworkPart() throws InvalidIpAddressException{
    assertEquals( "10101100", new IPAddress("172.16.254.1/8").getNetworkBitRepresentation());
  }
  
  public void testHostPart() throws InvalidIpAddressException{
    assertEquals( "000100001111111000000001", new IPAddress("172.16.254.1/8").getHostBitRepresentation());
  }
  
  public void testSameNetwork() throws InvalidIpAddressException{
    assertEquals( true, new IPAddress("192.168.1.1/24").isOnSameNetwork( new IPAddress("192.168.1.2/24") ));
    assertEquals( false, new IPAddress("192.168.1.1/24").isOnSameNetwork( new IPAddress("192.168.2.1/24") ));
  }
  
  public void testLocalAddress() throws InvalidIpAddressException{
    System.out.println(IPAddress.getLocalIPAddress());
  }
  
  public void testPattern(){
    Pattern thePattern = Pattern.compile( "(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})(?:/(\\d{1,2}))?" );
    assertTrue( thePattern.matcher( "192.168.1.150"  ).matches());
    assertFalse( thePattern.matcher( "192*168.1.150"  ).matches());
    assertTrue( thePattern.matcher( "192.168.1.150/24"  ).matches());
    assertFalse( thePattern.matcher( "192*168.1.150qdqf"  ).matches());

    Matcher theMatcher = thePattern.matcher( "192.168.1.150/24"  );
    if(theMatcher.matches()){
      System.out.println(theMatcher.groupCount());
      System.out.println(theMatcher.group( 1 ));
      for(int i=0;i<=theMatcher.groupCount();i++){
        System.out.println(i + "=" + theMatcher.group( i ));
      }
    }
    
    theMatcher = thePattern.matcher( "192.168.1.150"  );
    if(theMatcher.matches()){
      System.out.println(theMatcher.groupCount());
      for(int i=0;i<=theMatcher.groupCount();i++){
        System.out.println(i + "=" + theMatcher.group( i ));
      }
    }
  }
  
  public void testGetNetworkPrefixLengthForLocalIp() throws IOException{
    assertEquals("127.0.0.1/8", IPAddress.getIPAddressForLocalIP("127.0.0.1").getIpAddressWithNetworkPrefixLength());
//    assertEquals("192.168.1.3/24", IPAddress.getNetworkPrefixLengthForLocalIp("192.168.1.3").getIpAddressWithNetworkPrefixLength());
  }
}
