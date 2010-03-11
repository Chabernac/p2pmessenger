/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.pipe;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import chabernac.protocol.Protocol;
import chabernac.protocol.routing.Peer;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.RoutingTableEntry;
import chabernac.protocol.routing.UnkwownPeerException;
import chabernac.tools.IOTools;
import chabernac.tools.NetTools;

/**
 * The pipe protocol will be able to open a pipe between 2 peers.
 * The streams in the pipe(Input and Output stream) can then be used to directly 
 * transfer bytes between 2 peers.
 * This could be used to transfer large content between 2 peers like files.
 * 
 * The pipe protocol will make use of routing table to create the streams trough the network
 * 
 *  //TODO test first if the other peer has the pipe protocol 
 */
public class PipeProtocol extends Protocol {
  private static int PIPE_PORT = 43100;
  
  private Logger LOGGER = Logger.getLogger(PipeProtocol.class);
  
  private RoutingTable myRoutingTable = null;

  public static enum Command { OPEN_SOCKET };
  public static enum Result { UNKNOWN_COMMAND, PEER_UNREACHABLE, SOCKET_FAILURE, INVALID_PEERS, UNKNWOWN_PEER, SOCKET_OPENED };
  private ExecutorService myServerSocketExecutor = null;
  
  private IPipeListener myPipeListener = null;

  public PipeProtocol ( RoutingTable aRoutingTable, int aNumberOfServerSocketsAllowed ) {
    super( "PIP" );
    myRoutingTable = aRoutingTable;
    myServerSocketExecutor = Executors.newFixedThreadPool( aNumberOfServerSocketsAllowed );
  }

  @Override
  public String getDescription() {
    return "Pipe protocol";
  }

  @Override
  protected String handleCommand( long aSessionId, String anInput ) {
    if( anInput.startsWith( Command.OPEN_SOCKET.name() ) ){
      String[] thePeerIds = anInput.substring( Command.OPEN_SOCKET.name().length() ).trim().split( " " );
      
      if(thePeerIds.length != 2){
        LOGGER.error("Invalid peers received: '" + anInput + "'");
        return Result.INVALID_PEERS.name();
      }
      
      for(String thePeer : thePeerIds){
        if("".equals(thePeer)){
          LOGGER.error("Received invalid peer identifier: '" + thePeer + "' input='" + anInput + "'");
          return Result.INVALID_PEERS.name();
        } else if(myRoutingTable.getEntryForPeer(  thePeer  ) == null){
          LOGGER.error("Received unknwown peer identifier: '" + thePeer + "'");
          return Result.UNKNWOWN_PEER.name();
        }
      }
      
      RoutingTableEntry theToPeerEntry = myRoutingTable.getEntryForPeer( thePeerIds[1] );
      
      if(!theToPeerEntry.isReachable()){
        return Result.PEER_UNREACHABLE.name(); 
      } else {
        try{
          ServerSocket theSocket = NetTools.openServerSocket(PIPE_PORT);
          LOGGER.debug("Opening server socket on peer: '" + myRoutingTable.getLocalPeerId() + "' with port: '" + theSocket.getLocalPort() + "'");
          myServerSocketExecutor.submit( new ServerSocketHandler(theSocket, thePeerIds[0], thePeerIds[1]));
          Thread.yield();
          return Result.SOCKET_OPENED.name() + " " + Integer.toString( theSocket.getLocalPort() );
        }catch(Exception e){
          return Result.SOCKET_FAILURE.name();
        }
      }
    }
    return Result.UNKNOWN_COMMAND.name();
  }

  public IPipeListener getPipeListener() {
    return myPipeListener;
  }

  public void setPipeListener( IPipeListener anPipeListener ) {
    myPipeListener = anPipeListener;
  }

  @Override
  protected void stopProtocol() {
    myServerSocketExecutor.shutdownNow();
  }

  public void openPipe(Pipe aPipe) throws IOException, UnkwownPeerException{
    aPipe.setSocket( openSocketToPeer( myRoutingTable.obtainLocalPeer(), aPipe.getPeer() ) );
  }

  public void closePipe(Pipe aPipe){
    if(aPipe.getSocket() != null){
      try {
        aPipe.getSocket().close();
      } catch ( IOException e ) {
      }
    }
  }
  
  private Socket openSocketToPeer(Peer aFromPeer, Peer aToPeer) throws IOException, UnkwownPeerException{
    Peer theGateway = myRoutingTable.getGatewayForPeer(aToPeer);
    String theResult = theGateway.send( createMessage( Command.OPEN_SOCKET + " " + aFromPeer.getPeerId()  + " " + aToPeer.getPeerId()) );
    
    if(!theResult.startsWith( Result.SOCKET_OPENED.name() )){
      LOGGER.error("Socket with peer '" + aFromPeer.getPeerId() +  "' could not be openend: " + theResult);
      throw new IOException("Socket with peer '" + aFromPeer.getPeerId() + "' could not be openend: " + theResult);
    }

    int theSocketPort = Integer.parseInt( theResult.split( " " )[1]);
    
    Socket theSocket =  aFromPeer.createSocket( theSocketPort );
    if(theSocket == null){
      throw new IOException("Socket with peer: ' " + aFromPeer.getPeerId() + "' could not be established" );
    }
    return theSocket;
  }

  private class ServerSocketHandler implements Runnable{
    private String myFromPeerId;
    private String myToPeerId;
    private ServerSocket mySocket = null;

    public ServerSocketHandler(ServerSocket aSocket, String aFromPeerId, String aToPeerId){
      myFromPeerId = aFromPeerId;
      myToPeerId = aToPeerId;
      mySocket = aSocket;
    }

    public void run(){
      Socket theInSocket = null;
      Socket theOutSocket = null;

      try{
        theInSocket = mySocket.accept();

        if(myToPeerId.equals( myRoutingTable.getLocalPeerId()) && myPipeListener != null){
          Pipe thePipe = new Pipe(myRoutingTable.getEntryForPeer( myFromPeerId ).getPeer());
          thePipe.setSocket( theInSocket );
          myPipeListener.incomingPipe( thePipe );
        } else {

          //we are just a go between peer rerout the pipe to the destination
          theOutSocket = openSocketToPeer( myRoutingTable.obtainLocalPeer(), myRoutingTable.getEntryForPeer( myToPeerId ).getGateway() );
          if(theOutSocket == null){
            throw new IOException("Out socket with peer: '" + myToPeerId + "' could not be created");
          } else {
            Socket[] theSockets = new Socket[]{theInSocket, theOutSocket};
            //now link the in socket and the out socket together
            ExecutorService theCopyStreamService = Executors.newFixedThreadPool( 2 );
            theCopyStreamService.execute( new CopyStream(theInSocket.getInputStream(), theOutSocket.getOutputStream(), theSockets) );
            theCopyStreamService.execute(  new CopyStream(theOutSocket.getInputStream(), theInSocket.getOutputStream(), theSockets) );
          }

        }
      }catch(Exception e){
        if(theInSocket != null){
          try {
            theInSocket.close();
          } catch ( IOException e1 ) {
          }
        }
        if(theOutSocket != null){
          try {
            theOutSocket.close();
          } catch ( IOException e1 ) {
          }
        }
      }
    }
  }

  private class CopyStream implements Runnable{
    private InputStream myInputStream = null;
    private OutputStream myOutputStream = null;
    private Socket[] mySockets = null;

    public CopyStream(InputStream anInputStream, OutputStream anOutputStream, Socket[] aSockets){
      myInputStream = anInputStream;
      myOutputStream = anOutputStream;
      mySockets = aSockets;
    }

    public void run(){
      try {
        IOTools.copyStream( myInputStream, myOutputStream );
      } catch ( IOException e ) {}

      //when the copying of the streams is interrupted, close all sockets to end the socket stream
      for(Socket theSocket : mySockets){
        try{
          theSocket.close();
        }catch(IOException e){}
      }
    }
  }

}
