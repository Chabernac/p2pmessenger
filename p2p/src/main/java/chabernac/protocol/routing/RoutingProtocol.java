/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.activation.DataSource;

import org.apache.log4j.Logger;
import org.doomdark.uuid.UUIDGenerator;

import chabernac.io.Base64ObjectStringConverter;
import chabernac.io.ClassPathResource;
import chabernac.io.iObjectPersister;
import chabernac.io.iObjectStringConverter;
import chabernac.protocol.AlreadyRunningException;
import chabernac.protocol.Protocol;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.ServerInfo;
import chabernac.tools.IOTools;
import chabernac.tools.NetTools;

/**
 *  the routing protocol will do the following
 *  
 *  - send it's own routing table to all known peers in the routing table
 *  - update the routing table with the items received from another peer.  in this process the fasted path to a peer must be stored in the routing table
 *  - periodically contact all peers to see if they are still online and retrieve the routing table of the other peer.
 *  
 */

public class RoutingProtocol extends Protocol {
  public static String ID = "ROU";

  private static Logger LOGGER = Logger.getLogger( RoutingProtocol.class );

  public static final int START_PORT = 12700;
  public static final int END_PORT = 12720;
  public static final int MULTICAST_PORT = 13879;
  public static final String MULTICAST_ADDRESS = "234.5.54.9";

  public static enum Command { REQUEST_TABLE, WHO_ARE_YOU, ANNOUNCEMENT_WITH_REPLY, ANNOUNCEMENT };
  public static enum Response { OK, NOK, UNKNOWN_COMMAND };

  private RoutingTable myRoutingTable = null;

  private long myExchangeDelay;

  //this counter has just been added for unit testing reasons
  private AtomicLong myExchangeCounter = new AtomicLong(0);

  //this list is for test reasons to simulate peers which can not reach each other locally 
  private List<String> myUnreachablePeers = new ArrayList< String >();

  //this list is for test reasons to simulate peers which can not reach each other remotely
  //  private List<String> myRemoteUnreachablePeers = new ArrayList< String >();

  private ScheduledExecutorService mySheduledService = null;

  private iObjectPersister< RoutingTable > myObjectPersister = new RoutingTablePersister();

  private boolean isPersistRoutingTable = false;
  private boolean isStopWhenAlreadyRunning = false;

  private ExecutorService myChangeService = null;

  private String myLocalPeerId = null;

  private ExecutorService myUDPPacketHandlerService = Executors.newFixedThreadPool( 5 );
  private ExecutorService myScannerService = Executors.newCachedThreadPool( );

  private MulticastSocket myServerMulticastSocket = null;

  private IRoutingProtocolMonitor myRoutingProtocolMonitor = null;

  private boolean isPeerIdInFile = false;

  private DataSource mySuperNodesDataSource = new ClassPathResource("supernodes.txt");

  private ServerInfo myServerInfo = null;

  private final iObjectStringConverter< RoutingTable > myRoutingTableConverter = new Base64ObjectStringConverter< RoutingTable >();
  private final iObjectStringConverter< RoutingTableEntry > myRoutingTableEntryConverter = new Base64ObjectStringConverter< RoutingTableEntry >();

  /**
   * 
   * @param aLocalPeerId
   * @param aRoutingTable
   * @param anExchangeDelay the delay in seconds between exchaning routing tables with other peers
   */
  public RoutingProtocol ( String aLocalPeerId, long anExchangeDelay, boolean isPersistRoutingTable, DataSource aSuperNodesDataSource, boolean isStopWhenAlreadyRunning) throws ProtocolException{
    super( ID );
    myLocalPeerId = aLocalPeerId;
    myExchangeDelay = anExchangeDelay;
    this.isPersistRoutingTable = isPersistRoutingTable;
    this.isStopWhenAlreadyRunning = isStopWhenAlreadyRunning;

    if(myLocalPeerId != null && !"".equals( myLocalPeerId )){
      isPeerIdInFile = true;
    } else {
      isPeerIdInFile = false;
    }

    if(aSuperNodesDataSource != null){
      mySuperNodesDataSource = aSuperNodesDataSource;
    }

    loadRoutingTable();
  }

  public void start() throws ProtocolException{
    if(isStopWhenAlreadyRunning){
      try {
        Peer theLocalPeer = myRoutingTable.getEntryForLocalPeer().getPeer();
        //only do the check if the our port is not the same as the port from the routing table
        //if it is than we would check if we our running ourselfs, which is at this point stupid
        if(myServerInfo != null && myServerInfo.getServerPort() != theLocalPeer.getPort() && isAlreadyRunning(theLocalPeer)){
          throw new AlreadyRunningException(theLocalPeer);
        }
      } catch (UnknownPeerException e) {
        LOGGER.error("Could not get entry for local peer");
      }
    }

    resetRoutingTable();

    //add the entry for the local peer based on the server info
    if(myServerInfo != null){
      try{
        Peer theLocalPeer = new Peer(getLocalPeerId(), myServerInfo.getServerPort());
        RoutingTableEntry theLocalRoutingTableEntry = new RoutingTableEntry(theLocalPeer, 0, theLocalPeer);
        myRoutingTable.addRoutingTableEntry( theLocalRoutingTableEntry );
      }catch(NoAvailableNetworkAdapterException e){
        //TODO we should do something when the network adapter becomes available again
        LOGGER.error( "The local network adapter could not be located", e );
      }
    }

    if(myExchangeDelay > 0 ) scheduleRoutingTableExchange();
    myChangeService = Executors.newFixedThreadPool( 5 );

    myRoutingTable.addRoutingTableListener( new RoutingTableListener() );
    startUDPListener();
    saveRoutingTable();
  }

  /**
   * This method will try to contact a routing protocol that might be running at the port indicated in the routing table file
   * if there is a routing protocol already running at this port then return true
   */
  private boolean isAlreadyRunning(Peer aPeer) {
    LOGGER.debug("Checking if we're already running at: " + aPeer.getPort());
    return contactPeer(aPeer, myUnreachablePeers);
  }

  private void startUDPListener(){
    myUDPPacketHandlerService.execute( new MulticastServerThread() );
  }


  private void scheduleRoutingTableExchange(){
    mySheduledService = Executors.newScheduledThreadPool( 1 );
    mySheduledService.scheduleWithFixedDelay( new ScanLocalSystem(), 1, myExchangeDelay, TimeUnit.SECONDS);
    mySheduledService.scheduleWithFixedDelay( new ExchangeRoutingTable(), 2, myExchangeDelay, TimeUnit.SECONDS);
    mySheduledService.scheduleWithFixedDelay( new SendUDPAnnouncement(), 4, myExchangeDelay, TimeUnit.SECONDS);
    mySheduledService.schedule( new ScanFixedIpList(), 10, TimeUnit.SECONDS);
    mySheduledService.scheduleWithFixedDelay( new ScanRemoteSystem(), 20 , 4 * myExchangeDelay, TimeUnit.SECONDS);
    mySheduledService.scheduleWithFixedDelay( new DetectRemoteSystem(), 100, 10 * myExchangeDelay, TimeUnit.SECONDS);
  }

  @Override
  public String getDescription() {
    return "Routing protocol";
  }

  public List< String > getLocalUnreachablePeerIds() {
    return myUnreachablePeers;
  }

  //  public List< String > getRemoteUnreachablePeerIds() {
  //    return myRemoteUnreachablePeers;
  //  }

  public String getLocalPeerId(){
    if(myLocalPeerId != null && !"".equals( myLocalPeerId )){
      return myLocalPeerId;
    } else if(myRoutingTable != null){
      return myRoutingTable.getLocalPeerId();
    } else {
      myLocalPeerId = UUIDGenerator.getInstance().generateTimeBasedUUID().toString();
      return myLocalPeerId;
    }
  }

  @Override
  public String handleCommand( long aSessionId, String anInput ) {
    int theFirstIndexOfSpace = anInput.indexOf( " " );
    if(theFirstIndexOfSpace == -1) theFirstIndexOfSpace = anInput.length();
    String theCommandString = anInput.substring( 0,  theFirstIndexOfSpace);

    Command theCommand = Command.valueOf( theCommandString );
    try{
      if(theCommand == Command.REQUEST_TABLE){
        //another peer has send a request for the routing protocol send it
        return myRoutingTableConverter.toString( myRoutingTable );
      } else if(theCommand == Command.WHO_ARE_YOU){
        //another peer requested my peer id, send it to him, this is also used
        //to check if I'm still alive and kicking
        try{
          RoutingTableEntry theEntryForLocalPeer = myRoutingTable.getEntryForLocalPeer();
          return theEntryForLocalPeer.getPeer().getPeerId();
        }catch(Exception e){
          LOGGER.error( "Could not obtain entry for local peer", e );
          return Response.NOK.name();
        }
      } else if(theCommand == Command.ANNOUNCEMENT_WITH_REPLY){
        //the announcement is of the peer which is sending the annoucement
        //so the peer id inside the routingtable entry is also the containing peer
        String thePeerEntry = anInput.substring( theFirstIndexOfSpace + 1 );
        RoutingTableEntry theEntry = myRoutingTableEntryConverter.getObject( thePeerEntry ).incHopDistance();
        myRoutingTable.addRoutingTableEntry( theEntry);
        return myRoutingTableConverter.toString( myRoutingTable );
      } else if(theCommand == Command.ANNOUNCEMENT){
        String[] theAttributes = anInput.substring( theFirstIndexOfSpace + 1 ).split(";");

        RoutingTableEntry theSendingPeer = myRoutingTableEntryConverter.getObject( theAttributes[0] );
        //we add the sending peer to the routing table
        //      myRoutingTable.addRoutingTableEntry(theSendingPeer.incHopDistance());

        RoutingTableEntry thePeer = myRoutingTableEntryConverter.getObject( theAttributes[1] );

        //the sending peer has send the entry so we set it as gateway and increment the hop distance
        thePeer = thePeer.entryForNextPeer( theSendingPeer.getPeer() );
        myRoutingTable.addRoutingTableEntry( thePeer );
        return Response.OK.name();
      }
    }catch(IOException e){
      LOGGER.error("An error occured while parsing Routing table or entries to string or visa verca", e);
      return Response.NOK.name();
    }
    return Response.UNKNOWN_COMMAND.name();
  }

  public RoutingTable getRoutingTable(){
    return myRoutingTable;
  }

  boolean contactPeer(Peer aPeer, List<String> anUnreachablePeers){
    try{
      LOGGER.debug("Sending message to '" + aPeer.getHosts() + "' port '" + aPeer.getPort() + "'");
      String[] theIdTime = aPeer.send( createMessage( Command.WHO_ARE_YOU.name() )).split( " " );

      if(!anUnreachablePeers.contains( theIdTime[0] )){
        aPeer.setPeerId( theIdTime[0] );
        RoutingTableEntry theEntry = new RoutingTableEntry(aPeer, 1, aPeer);

        LOGGER.debug("Detected system on '" + aPeer.getHosts() + "' port '" + aPeer.getPort() + "'");
        //only if we have detected our self we set the hop distance to 0
        if(theIdTime[0].equals(myRoutingTable.getLocalPeerId())){
          theEntry = theEntry.derivedEntry( 0 );
        }
        myRoutingTable.addRoutingTableEntry( theEntry );
        return true;
      }
    }catch(Exception e){
      //TODO remove extensive logging
      //      try {
      //        if(aPeer.getPort() == RoutingProtocol.START_PORT && aPeer.getHosts().get( 0 ).equalsIgnoreCase( InetAddress.getLocalHost().getHostAddress() )){
//      LOGGER.error( "Error occured while contacting peer '" + aPeer.getPeerId() + "' " + aPeer.getHosts() + ": " + aPeer.getPort() );
      //        }
      //      } catch ( UnknownHostException e1 ) {
      //        e1.printStackTrace();
      //      }
    }
    return false;
  }

  public void scanLocalSystem(){
    if(myRoutingProtocolMonitor != null) myRoutingProtocolMonitor.localSystemScanStarted();
    try{
      LOGGER.debug( "Scanning local system" );
      List<String> theLocalHosts = NetTools.getLocalExposedIpAddresses();
      for(int i=START_PORT;i<=END_PORT;i++){
        myScannerService.execute( new ScanSystem(this, theLocalHosts, i, myUnreachablePeers));
      }
    }catch(SocketException e){
      LOGGER.error( "Could not get local ip addressed", e );
    }  
  }

  /**
   * this method will scan the routing table and find hosts which are unreachable
   * for this hosts a port scan will be started to detect if the peer is not online
   * on a different port, if one is found, the port scan stops
   */
  public void scanRemoteSystem(boolean isExcludeLocal){
    if(myRoutingProtocolMonitor != null) myRoutingProtocolMonitor.remoteSystemScanStarted();
    //first search all hosts which have no single peer
    Map<String, Boolean> theHosts = new HashMap< String, Boolean >();

    for(RoutingTableEntry theEntry : myRoutingTable){
      if(isExcludeLocal && !theEntry.getPeer().getPeerId().equals(myRoutingTable.getLocalPeerId())){
        for(String theHost : theEntry.getPeer().getHosts()){
          boolean isReachable = false;
          if(theHosts.containsKey( theHost )){
            isReachable = theHosts.get(theHost);
          }
          theHosts.put( theHost, isReachable | theEntry.isReachable() );
        }
      }
    }

    //now try to scan all hosts which are not reachable
    for(String theHost : theHosts.keySet()){
      if(!theHosts.get(theHost)){
        //this host is not reachable, scan it
        boolean isContacted = false;
        for(int i=START_PORT;i<=END_PORT && !isContacted;i++){
          try{
            if(!isExcludeLocal || i!=myRoutingTable.getEntryForLocalPeer().getPeer().getPort()){
              LOGGER.debug("Scanning the following host: '" + theHost + "' on port '" + i + "'");
              isContacted = contactPeer( new Peer("", theHost, i), myUnreachablePeers );
            }
          }catch(Exception e){}
        }
      }
    }
  }

  /**
   * this function will look at the local ip adres and start port scanning in the same range for other peers
   * we only do this if there are no ohter peers in the network but our selfs
   */
  public void detectRemoteSystem(){
    if(myRoutingProtocolMonitor != null) myRoutingProtocolMonitor.detectingRemoteSystemStarted();
    if(myRoutingTable.getNrOfReachablePeers() <= 1){
      try{
        InetAddresIterator theIterator = new InetAddresIterator(InetAddress.getLocalHost(), 24);
        while(myRoutingTable.getNrOfReachablePeers() <= 1 && theIterator.hasNext()){
          ScanSystem theScanSystem = new ScanSystem(this, theIterator.next(), START_PORT);
          theScanSystem.setCondition( new NrOfPeersSmallerThenCondition(myRoutingTable, 1) );
          myScannerService.execute( theScanSystem );
        }
      }catch(Exception e ){
        LOGGER.error( "An error occured while scanning system", e );
      }
    }
  }

  private class ScanLocalSystem implements Runnable{
    public void run(){
      scanLocalSystem();
    }
  }

  private class ScanRemoteSystem implements Runnable{
    public void run(){
      scanRemoteSystem(false);
    }
  }

  public void scanSuperNodes(){
    if(myRoutingProtocolMonitor != null) myRoutingProtocolMonitor.scanningSuperNodes();
    try{
      List<String> theIps = IOTools.loadStreamAsList( mySuperNodesDataSource.getInputStream() );
      for(String theIp : theIps){
        myScannerService.execute( new ScanSystem(RoutingProtocol.this, theIp, START_PORT, myUnreachablePeers));
      }
    }catch(IOException e){
      LOGGER.error("An error occured while scanning super nodes", e);
    }
  }

  /**
   * this method will send a request to all the peers in the routing table
   */
  public void exchangeRoutingTable(){
    if(myRoutingProtocolMonitor != null) myRoutingProtocolMonitor.exchangingRoutingTables();
    LOGGER.debug("Exchanging routing table for peer: " + myRoutingTable.getLocalPeerId());

    for(RoutingTableEntry theEntry : myRoutingTable.getEntries()){
      Peer thePeer = theEntry.getPeer();

      //simulate that we can not contact this peer directly, but we can contact it trough a gateway
      //so check if hop distance = 1


      if(!thePeer.getPeerId().equals(myRoutingTable.getLocalPeerId())){
        try {
          if(myUnreachablePeers.contains( thePeer.getPeerId())){
            //simulate that we cannot contact the peer
            throw new Exception("Simulate that we can not contact peer: " + thePeer.getPeerId());
          }
          String theTable = thePeer.send( createMessage( Command.ANNOUNCEMENT_WITH_REPLY.name() + " "  + myRoutingTableEntryConverter.toString( myRoutingTable.getEntryForLocalPeer() ))) ;
          //          String theTable = thePeer.send( createMessage( Command.REQUEST_TABLE.name() ));
          RoutingTable theRemoteTable = myRoutingTableConverter.getObject( theTable );

          if(!theRemoteTable.getLocalPeerId().equals( thePeer.getPeerId() )){
            //if we get here it means that another peer has taken the place of the previous peer,
            //i.e. it is running on the same host and port
            //this means that the peer is not reachable any more
            RoutingTableEntry theOldEntry = theEntry.derivedEntry( RoutingTableEntry.MAX_HOP_DISTANCE );
            myRoutingTable.removeRoutingTableEntry( theOldEntry );
          }

          //test that we did not take the place of another peer on the same host and port
          if(!myLocalPeerId.equals( theRemoteTable.getLocalPeerId() )){

            myRoutingTable.merge( theRemoteTable );
            //we can connect directly to this peer, so the hop distance is 1
            //theEntry.setHopDistance( 1 );
            RoutingTableEntry theEntryOfRemotePeer = myRoutingTable.getEntryForPeer( theRemoteTable.getLocalPeerId() );


            //          //TODO remove
            //          if(myLocalPeerId.equals( theEntryOfRemotePeer.getPeer().getPeerId() )){
            //            //we should never get here
            //            throw new RuntimeException("we contacted our selfs");
            //          }

            myRoutingTable.addRoutingTableEntry( theEntryOfRemotePeer.derivedEntry( 1 ) );
          }
        } catch ( Exception e ) {
          //update all peers which have this peer as gateway to the max hop distance
          for(RoutingTableEntry theEntry2 : myRoutingTable.getEntries()){
            if(theEntry2.getGateway().getPeerId().equals( theEntry.getPeer().getPeerId())){
              //              theEntry2.setHopDistance( RoutingTableEntry.MAX_HOP_DISTANCE );
              myRoutingTable.addRoutingTableEntry( theEntry2.derivedEntry( RoutingTableEntry.MAX_HOP_DISTANCE ) );
            }
          }
        }
      }
    }
    myExchangeCounter.incrementAndGet();
    LOGGER.debug("End exchanging routing table for peer: " + myRoutingTable.getLocalPeerId());

    //save the routing table
    if(isPersistRoutingTable) saveRoutingTable();
  }

  private void sendAnnoucement( RoutingTableEntry anEntry ) {
    try{
      if(myRoutingTable.getEntryForLocalPeer() == null){
        //we're not able to send announcment yet because we have not detected our selfs
        return;
      }
    }catch(Exception e){
      LOGGER.error("Could not get entry for local peer", e);
    }

    for(RoutingTableEntry theEntry : myRoutingTable.getEntries()){
      Peer thePeer = theEntry.getPeer();

      //do not send the entry to our selfs, we already have the entry
      if(!thePeer.getPeerId().equals( myRoutingTable.getLocalPeerId()) &&
          //only send announcements to our neighbours, this means no peers with a hop distance > 1
          theEntry.getHopDistance() <= 1 &&
          //also do not send the entry to the peer from which the entry is coming.
          !theEntry.getPeer().getPeerId().equals( anEntry.getPeer().getPeerId() ) &&
          //do not send announcement to peers we cannot reach in test mode
          !myUnreachablePeers.contains(thePeer.getPeerId())){
        try {
          LOGGER.debug("Sending announcement of peer '" + anEntry.getPeer().getPeerId() +  "' from peer '" + myLocalPeerId +  "' to peer '" + thePeer.getPeerId() + "' on '" + thePeer.getHosts()  + ": "  +  thePeer.getPort() + "'");
          String theResult = thePeer.send( createMessage( Command.ANNOUNCEMENT.name() + " "  + myRoutingTableEntryConverter.toString( myRoutingTable.getEntryForLocalPeer()) + ";" + myRoutingTableEntryConverter.toString( anEntry ))) ;
          if(!Response.OK.name().equals( theResult )){
            throw new Exception("Unexpected result code '" + theResult + "'");
          }
        } catch ( Exception e ) {
          //the peer can not be reached 
          //update all peers which have this peer as gateway to the max hop distance
          for(RoutingTableEntry theEntry2 : myRoutingTable.getEntries()){
            if(theEntry2.getGateway().getPeerId().equals( theEntry.getPeer().getPeerId())){
              //              theEntry2.setHopDistance( RoutingTableEntry.MAX_HOP_DISTANCE );
              myRoutingTable.addRoutingTableEntry( theEntry2.derivedEntry( RoutingTableEntry.MAX_HOP_DISTANCE ) );
            }
          }
        }
      }
    }

  }

  public long getExchangeCounter(){
    return myExchangeCounter.longValue();
  }

  private class ExchangeRoutingTable implements Runnable{

    @Override
    public void run() {
      exchangeRoutingTable();
    }
  }

  private File getRoutingTableLocation(){
    String theFile = "RoutingTable";
    if(isPeerIdInFile){
      theFile += "_" + myLocalPeerId;
    }
    theFile += ".csv";
    return new File(theFile);
  }

  private void loadRoutingTable(){
    if(isPersistRoutingTable && getRoutingTableLocation().exists()){
      File theFile = getRoutingTableLocation();
      try{
        FileInputStream theInputStream = new FileInputStream(theFile);
        myRoutingTable = myObjectPersister.loadObject( theInputStream );
        theInputStream.close();
      }catch(Exception e){
        LOGGER.error( "Could not load routing table", e );
      }
    } else {
      myRoutingTable = new RoutingTable(getLocalPeerId());
    }
  }

  public void resetRoutingTable(){
    for(RoutingTableEntry theEntry : myRoutingTable.getEntries()){
      myRoutingTable.addRoutingTableEntry( theEntry.derivedEntry( RoutingTableEntry.MAX_HOP_DISTANCE ) );
      //      theEntry.setHopDistance( RoutingTableEntry.MAX_HOP_DISTANCE );
    }
  }

  private void saveRoutingTable(){
    File theFile = getRoutingTableLocation();
    try{
      FileOutputStream theStream = new FileOutputStream(theFile);
      myObjectPersister.persistObject( myRoutingTable, theStream );
      theStream.flush();
      theStream.close();
    }catch(Exception e){
      LOGGER.error("Unable to save routing table", e);
    }
  }

  @Override
  public void stop() {
    //remove all listeners from the routing table
    myRoutingTable.removeAllroutingTableListeners();

    if(myScannerService != null){
      myScannerService.shutdownNow();
    }

    if(mySheduledService != null){
      mySheduledService.shutdownNow();
    }

    if(myChangeService != null) {
      myChangeService.shutdownNow();
    }

    if(myUDPPacketHandlerService != null){
      myUDPPacketHandlerService.shutdownNow();
    }

    if(isPersistRoutingTable) saveRoutingTable();

    if(myServerMulticastSocket != null){
      myServerMulticastSocket.close();
    }

    //announce that we leave the P2P network
    try{
      RoutingTableEntry theSelfRoutingTableEntry = getRoutingTable().getEntryForLocalPeer();
      if(theSelfRoutingTableEntry != null){
        theSelfRoutingTableEntry = theSelfRoutingTableEntry.derivedEntry( RoutingTableEntry.MAX_HOP_DISTANCE );
        sendAnnoucement( theSelfRoutingTableEntry );
      }
    } catch ( UnknownPeerException e ) {
    }
  }

  private class RoutingTableListener implements IRoutingTableListener{
    @Override
    public void routingTableEntryChanged( RoutingTableEntry anEntry ) {
      myChangeService.execute( new SendAnnouncement(anEntry) );
    }
  }

  private class SendAnnouncement implements Runnable{
    private RoutingTableEntry myEntry = null;

    public SendAnnouncement(RoutingTableEntry anEntry){
      myEntry = anEntry;
    }

    public void run(){
      sendAnnoucement(myEntry); 
    }
  }

  private class MulticastServerThread implements Runnable{
    @Override
    public void run() {
      try{
        myServerMulticastSocket = new MulticastSocket(MULTICAST_PORT);
        InetAddress theGroup = InetAddress.getByName(MULTICAST_ADDRESS);
        myServerMulticastSocket.joinGroup(theGroup);

        while(!myServerMulticastSocket.isClosed()){
          byte[] theBytes = new byte[1024];
          DatagramPacket thePacket = new DatagramPacket(theBytes, theBytes.length);
          myServerMulticastSocket.receive( thePacket );
          ObjectInputStream theObjectInputStream = new ObjectInputStream(new ByteArrayInputStream(theBytes));
          Object theObject = theObjectInputStream.readObject();
          if(theObject instanceof RoutingTableEntry){
            RoutingTableEntry theEntry = (RoutingTableEntry)theObject;
            if(!myUnreachablePeers.contains(theEntry.getPeer().getPeerId())){
              if(!theEntry.getPeer().getPeerId().equals( myLocalPeerId )){
                theEntry = theEntry.incHopDistance();
              }
              myRoutingTable.addRoutingTableEntry( theEntry );
            }
          }
        }
      }catch(SocketException e){
        if(!"socket closed".equalsIgnoreCase( e.getMessage())){
          LOGGER.error("Could not start udp server", e);
        }
      }catch(Exception e){
        LOGGER.error("Could not start udp server", e);
      }
    }
  }

  public void sendUDPAnnouncement(){
    if(myRoutingProtocolMonitor != null) myRoutingProtocolMonitor.sendingUDPAnnouncement();
    try{
      ByteArrayOutputStream theByteArrayOutputStream = new ByteArrayOutputStream();
      ObjectOutputStream theObjectOutputStream = new ObjectOutputStream(theByteArrayOutputStream);
      theObjectOutputStream.writeObject( myRoutingTable.getEntryForLocalPeer() );

      MulticastSocket theMulticastSocket = new MulticastSocket(MULTICAST_PORT);
      InetAddress theGroup = InetAddress.getByName(MULTICAST_ADDRESS);
      theMulticastSocket.joinGroup(theGroup);

      byte[] theBytes = theByteArrayOutputStream.toByteArray();
      DatagramPacket thePacket = new DatagramPacket(theBytes, theBytes.length, theGroup, MULTICAST_PORT);
      thePacket.setAddress( theGroup );

      theMulticastSocket.send( thePacket );
    }catch(Exception e){
      LOGGER.error( "Could not send datagram packet", e);
    }
  }

  private class SendUDPAnnouncement implements Runnable{

    @Override
    public void run() {
      sendUDPAnnouncement();
    }
  } 

  private class DetectRemoteSystem implements Runnable{

    @Override
    public void run() {
      detectRemoteSystem();
    }
  }

  public IRoutingProtocolMonitor getRoutingProtocolMonitor() {
    return myRoutingProtocolMonitor;
  }

  public void setRoutingProtocolMonitor( IRoutingProtocolMonitor anRoutingProtocolMonitor ) {
    myRoutingProtocolMonitor = anRoutingProtocolMonitor;
  }

  public class ScanFixedIpList implements Runnable {

    @Override
    public void run() {
      scanSuperNodes();
    }
  }

  @Override
  public void setServerInfo( ServerInfo aServerInfo ) throws ProtocolException {
    myServerInfo = aServerInfo;
    start();
  }
}
