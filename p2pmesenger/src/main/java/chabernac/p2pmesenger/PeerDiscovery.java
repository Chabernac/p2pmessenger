/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2pmesenger;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.AdvertisementFactory;
import net.jxta.endpoint.EndpointAddress;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.pipe.PipeService;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.PipeAdvertisement;

public class PeerDiscovery {
  private static final String ADV_NAME = "Peer discovery";
  private NetworkManager myNetworkManager = null;

  public PeerDiscovery(String aClientName) throws PeerGroupException, IOException{
    initNetwork(aClientName);
    startAdvertising();
    startDetecting();
  }

  private void initNetwork(String aClientName) throws IOException, PeerGroupException {
    
    myNetworkManager = new NetworkManager(NetworkManager.ConfigMode.ADHOC, aClientName,
                                          new File(new File(".cache"), aClientName).toURI());
    myNetworkManager.startNetwork();
  }

  private void startAdvertising() {
    PeerGroup theNetPeerGroup = myNetworkManager.getNetPeerGroup();

    // get the discovery service
    DiscoveryService theDiscoveryService = theNetPeerGroup.getDiscoveryService();

    ScheduledExecutorService theService = Executors.newScheduledThreadPool( 1 );
    theService.scheduleAtFixedRate( new AdvertisingThread(theDiscoveryService), 0, 2, TimeUnit.SECONDS );
  }

  private void startDetecting() {
    // get the discovery service
    DiscoveryService theDiscoveryService = myNetworkManager.getNetPeerGroup().getDiscoveryService();

    ScheduledExecutorService theService = Executors.newScheduledThreadPool( 1 );
    theService.scheduleAtFixedRate( new DiscoveryThread(theDiscoveryService), 0, 2, TimeUnit.SECONDS );
  }

  private class AdvertisingThread implements Runnable{
    private DiscoveryService myDiscoveryService = null;

    public AdvertisingThread(DiscoveryService aDiscoveryService){
      myDiscoveryService = aDiscoveryService;
    }

    @Override
    public void run() {
      System.out.println("Advertising....");
      long lifetime = 60 * 2 * 1000L;
      long expiration = 60 * 2 * 1000L;

      try{
        PipeAdvertisement theAvertisement = createPipeAdvertisement();
        myDiscoveryService.publish(theAvertisement, lifetime, expiration);
        myDiscoveryService.remotePublish(theAvertisement, expiration);
      }catch(IOException e){
        e.printStackTrace();
      }
    }

    private PipeAdvertisement createPipeAdvertisement() {
      PipeAdvertisement advertisement = (PipeAdvertisement)AdvertisementFactory.newAdvertisement(PipeAdvertisement.getAdvertisementType());

      advertisement.setPipeID(IDFactory.newPipeID(PeerGroupID.defaultNetPeerGroupID));
      advertisement.setType(PipeService.UnicastType);
      advertisement.setName(ADV_NAME);
      return advertisement;
    }
  }
  
  public void addDiscoveryListener(DiscoveryListener aListener){
    myNetworkManager.getNetPeerGroup().getDiscoveryService().addDiscoveryListener( aListener );
  }

  private class DiscoveryThread implements Runnable, DiscoveryListener{
    private DiscoveryService myDiscoveryService = null;

    public DiscoveryThread(DiscoveryService aDiscoveryService){
      myDiscoveryService = aDiscoveryService;
      myDiscoveryService.addDiscoveryListener(this);
    }

    @Override
    public void run() {
      System.out.println("Retrieving advertisements");
      myDiscoveryService.getRemoteAdvertisements(
                                                 // no specific peer (propagate)
                                                 null,
                                                 // Adv type
                                                 DiscoveryService.ADV,
                                                 // Attribute = name
                                                 "Name",
                                                 // Value = the tutorial
                                                 ADV_NAME,
                                                 // one advertisement response is all we are looking for
                                                 1,
                                                 // no query specific listener. we are using a global listener
                                                 null);
    }

    @Override
    public void discoveryEvent( DiscoveryEvent anEvent ) {
      System.out.println("Discovery event: " + anEvent.getSource().getClass());
      EndpointAddress theAddress = (EndpointAddress)anEvent.getSource();
      System.out.println(theAddress.getProtocolAddress() + " " + theAddress.getProtocolName() + " " + theAddress.getServiceName() + " " + theAddress.getServiceParameter());
    }

  }
  
  public static void main(String[] args){
    System.out.println("Starting discovery with peer name: '" + args[0] + "'");
    try {
      new PeerDiscovery(args[0]);
    } catch ( Exception e ) {
      e.printStackTrace();
    }
  }
}
