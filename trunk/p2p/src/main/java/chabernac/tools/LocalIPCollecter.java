/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.tools;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class LocalIPCollecter {
  private static Logger LOGGER = Logger.getLogger(LocalIPCollecter.class);
  
  private List< iIPListener > myListeners = new ArrayList< iIPListener >();
  private List<InetAddress> myIpList = new ArrayList<InetAddress>();
  
  private final long myTimeoutInSeconds;
  
  private final iNetworkInterfaceFilter myFilter;
  
  private ScheduledExecutorService myService = null;
  
  
  public LocalIPCollecter(iNetworkInterfaceFilter aFilter, long aTimeoutInSeconds){
    if(aFilter == null){
      myFilter = new DefaultNetworkInterfaceFilter();
    } else {
      myFilter = aFilter;
    }
    
    myTimeoutInSeconds = aTimeoutInSeconds;
  }
  
  public synchronized void start(){
    myService = Executors.newScheduledThreadPool( 1 );
    myService.scheduleWithFixedDelay( new Detect(), myTimeoutInSeconds, myTimeoutInSeconds, TimeUnit.SECONDS);
  }
  
  public synchronized void stop() {
    if(myService != null){
      myService.shutdownNow();
    }
  }
  
  public synchronized void clear(){
    myIpList.clear();
  }

  public synchronized void detectIPs() throws SocketException {
    List<InetAddress> theIpList = new ArrayList<InetAddress>();
    Enumeration<NetworkInterface> theInterfaces = NetworkInterface.getNetworkInterfaces();
    while(theInterfaces.hasMoreElements()){
      NetworkInterface theInterface = theInterfaces.nextElement();
//      print(theInterface);
      if(myFilter.isMatchingInterface(  theInterface )){
        Enumeration<InetAddress> theAddresses = theInterface.getInetAddresses();
        while(theAddresses.hasMoreElements()){
          theIpList.add(theAddresses.nextElement());
        }
      }
    }
    compareLists(myIpList, theIpList);
    myIpList = theIpList;
  }
  
  public List<InetAddress> getIPList(){
    return Collections.unmodifiableList( myIpList );
  }

  private void compareLists( List< InetAddress > aBeforeList, List< InetAddress > anAfterList ) {
    for(InetAddress theIP : aBeforeList){
      if(!anAfterList.contains( theIP )){
        informRemoved(theIP);
      }
    }
    
    for(InetAddress theIP : anAfterList){
      if(!aBeforeList.contains( theIP )){
        informAdded(theIP);
      }
    }
  }

  private void informRemoved( InetAddress anIp ) {
    for(iIPListener theListener : myListeners){
      theListener.IPRemoved( anIp );
    }
  }
  
  private void informAdded( InetAddress anIp ) {
    for(iIPListener theListener : myListeners){
      theListener.newIPBound( anIp );
    }
  }
  
  public void addIPListener(iIPListener aListener){
    myListeners.add(aListener);
  }
  
  public void removeIPListener(iIPListener aListener){
    myListeners.remove( aListener );
  }
  
  
  public class Detect implements Runnable {
    @Override
    public void run() {
      try {
        detectIPs();
      } catch ( SocketException e ) {
        LOGGER.error("An error occured while detecting ips");
      }
    }
  }
}
