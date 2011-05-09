/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.pipe;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import chabernac.io.SocketProxy;
import chabernac.p2p.settings.P2PSettings;
import chabernac.protocol.Protocol;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.routing.AbstractPeer;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.RoutingTableEntry;
import chabernac.protocol.routing.SocketPeer;
import chabernac.protocol.routing.UnknownPeerException;
import chabernac.protocol.routing.iPeerSender;
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
  public static final String ID = "PIP";

  private static int PIPE_PORT = 43100;

  private Logger LOGGER = Logger.getLogger(PipeProtocol.class);

  public static enum Command { OPEN_SOCKET };
  public static enum Result { UNKNOWN_COMMAND, PEER_UNREACHABLE, SOCKET_FAILURE, INVALID_PEERS, UNKNWOWN_PEER, SOCKET_OPENED };
  private ExecutorService myServerSocketExecutor = null;

  private Set<IPipeListener> myPipeListener = new HashSet< IPipeListener >();

  public PipeProtocol ( int aNumberOfServerSocketsAllowed ) {
    super( ID );
    myServerSocketExecutor = Executors.newFixedThreadPool( aNumberOfServerSocketsAllowed );
  }

  @Override
  public String getDescription() {
    return "Pipe protocol";
  }

  public RoutingTable getRoutingTable() throws ProtocolException{
    return ((RoutingProtocol)findProtocolContainer().getProtocol( RoutingProtocol.ID )).getRoutingTable();
  }
  
  public iPeerSender getPeerSender() throws ProtocolException{
    return ((RoutingProtocol)findProtocolContainer().getProtocol( RoutingProtocol.ID )).getPeerSender();
  }


  @Override
  public String handleCommand( String aSessionId, String anInput ) {
    if( anInput.startsWith( Command.OPEN_SOCKET.name() ) ){
      String[] theAttributes = anInput.substring( Command.OPEN_SOCKET.name().length() + 1 ).trim().split( ";" );

      if(theAttributes.length != 3){
        LOGGER.error("Invalid peer attributes: '" + anInput + "'");
        return Result.INVALID_PEERS.name();
      }

      try{
        for(int i=0;i<=1;i++){
          if("".equals(theAttributes[i])){
            LOGGER.error("Received invalid peer identifier: '" + theAttributes[i] + "' input='" + anInput + "'");
            return Result.INVALID_PEERS.name();
          } else if(getRoutingTable().getEntryForPeer(  theAttributes[i]  ) == null){
            LOGGER.error("Received unknwown peer identifier: '" + theAttributes[i] + "'");
            return Result.UNKNWOWN_PEER.name();
          }
        }

        RoutingTableEntry theFromPeerEntry = getRoutingTable().getEntryForPeer( theAttributes[0] );
        RoutingTableEntry theToPeerEntry = getRoutingTable().getEntryForPeer( theAttributes[1] );

        if(!theToPeerEntry.isReachable()){
          return Result.PEER_UNREACHABLE.name(); 
        } else {
          try{
            ServerSocket theSocket = NetTools.openServerSocket(PIPE_PORT);
            LOGGER.debug("Opening server socket on peer: '" + getRoutingTable().getLocalPeerId() + "' with port: '" + theSocket.getLocalPort() + "'");
            SocketProxy theSocketToNextPeer = null;
            if(!theToPeerEntry.getPeer().getPeerId().equals( getRoutingTable().getLocalPeerId())){
              //this peer is just an intermediate peer, create a new connection to next peer in the chain
              //TODO we should not just cast the peer to SocketPeer
              theSocketToNextPeer = openSocketToPeer( (SocketPeer)theFromPeerEntry.getPeer(), (SocketPeer)theToPeerEntry.getPeer(), theAttributes[2] );
            }
            myServerSocketExecutor.submit( new ServerSocketHandler(theSocket, (SocketPeer)theFromPeerEntry.getPeer(), theSocketToNextPeer, theAttributes[2]));
            Thread.yield();
            return Result.SOCKET_OPENED.name() + " " + Integer.toString( theSocket.getLocalPort() );
          }catch(Exception e){
            LOGGER.error("Could not open server socket at port", e);
            return Result.SOCKET_FAILURE.name();
          }
        }
      }catch(ProtocolException e){
        return ProtocolContainer.Response.UNKNOWN_PROTOCOL.name();
      } catch ( UnknownPeerException e ) {
        LOGGER.error("Received unknwown peer identifier: '" + theAttributes[1] + "'");
        return Result.UNKNWOWN_PEER.name();

      }
    }
    return Result.UNKNOWN_COMMAND.name();
  }

  public void addPipeListener( IPipeListener anPipeListener ) {
    myPipeListener.add( anPipeListener );
  }

  public void removePipeListener( IPipeListener anPipeListener ) {
    myPipeListener.remove( anPipeListener );
  }

  @Override
  public void stop() {
    myServerSocketExecutor.shutdownNow();
  }

  public Pipe openPipe(String aPeerId, String aPipeDescription) throws PipeException{
    try{
      AbstractPeer thePeer = getRoutingTable().getEntryForPeer( aPeerId ).getPeer();
      if(!(thePeer instanceof SocketPeer)) throw new PipeException("Can only open a pipe to a socket peer");

      Pipe thePipe = new Pipe((SocketPeer)thePeer);
      thePipe.setPipeDescription( aPipeDescription );
      openPipe( thePipe );
      return thePipe;
    }catch(Exception e){
      throw new PipeException("Could not open pipe", e);
    }
  }

  public void openPipe(Pipe aPipe) throws PipeException{
    if(aPipe.getPeer() == null) throw new PipeException("Destionation peer must be filled");
    if(aPipe.getPipeDescription() == null || "".equals( aPipe.getPipeDescription() )) throw new PipeException("Pipe description must be filled");

    try {
      //TODO we probably should not just cast the peer to SocketPeer
      aPipe.setSocket( openSocketToPeer( (SocketPeer)getRoutingTable().getEntryForLocalPeer().getPeer(), aPipe.getPeer(), aPipe.getPipeDescription() ) );
    } catch ( Exception e ) {
      throw new PipeException("Could not open pipe", e);
    }
  }

  public void closePipe(Pipe aPipe){
    if(aPipe.getSocket() != null){
      P2PSettings.getInstance().getSocketPool().close( aPipe.getSocket() );
    }
  }

  private SocketProxy openSocketToPeer(SocketPeer aFromPeer, SocketPeer aToPeer, String aPipeDescription) throws IOException, UnknownPeerException, ProtocolException{
    AbstractPeer theGateway = getRoutingTable().getGatewayForPeer(aToPeer);
    String theResult = getPeerSender().send(theGateway, createMessage( Command.OPEN_SOCKET + ";" + aFromPeer.getPeerId()  + ";" + aToPeer.getPeerId() + ";" +  aPipeDescription) );

    if(!theResult.startsWith( Result.SOCKET_OPENED.name() )){
      LOGGER.error("Peer: " + getRoutingTable().getLocalPeerId() + " Socket with peer '" + aToPeer.getPeerId() +  "' could not be openend: " + theResult);
      throw new IOException("Peer: " + getRoutingTable().getLocalPeerId() + " Socket with peer '" + aToPeer.getPeerId() + "' could not be openend: " + theResult);
    }

    int theSocketPort = Integer.parseInt( theResult.split( " " )[1]);

    SocketProxy theSocket =  aFromPeer.createSocket( theSocketPort );
    if(theSocket == null){
      throw new IOException("Socket with peer: ' " + aFromPeer.getPeerId() + "' could not be established" );
    }
    return theSocket;
  }

  private class ServerSocketHandler implements Runnable{
    private ServerSocket mySocket = null;
    private String myPipeDescription = null;
    private SocketProxy mySocketToNextPeer = null;
    private SocketPeer myFromPeer = null;

    public ServerSocketHandler(ServerSocket aSocket, SocketPeer aFromPeer, SocketProxy aSocketToNextPeer, String aPipeDescription){
      mySocket = aSocket;
      myPipeDescription = aPipeDescription;
      mySocketToNextPeer = aSocketToNextPeer;
      myFromPeer = aFromPeer;
    }

    public void run(){
      SocketProxy theInSocket = null;

      try{
        theInSocket = new SocketProxy( mySocket.accept() );

        //if mySocketToNextPeer is null it means that this peer is the end of the pipe
        //so if a socket is accepted we notify the listeners that a pipe has been made
        if(mySocketToNextPeer == null && myPipeListener != null){
          Pipe thePipe = new Pipe(myFromPeer);
          thePipe.setPipeDescription(myPipeDescription);
          thePipe.setSocket( theInSocket );
          for(IPipeListener theListener : myPipeListener){
            theListener.incomingPipe( thePipe );
          }
        } else {

          //we are just a go between peer rerout the pipe to the destination
          SocketProxy[] theSockets = new SocketProxy[]{theInSocket, mySocketToNextPeer};
          //now link the in socket and the out socket together
          ExecutorService theCopyStreamService = Executors.newFixedThreadPool( 2 );
          theCopyStreamService.execute( new CopyStream(theInSocket.getInputStream(), mySocketToNextPeer.getOutputStream(), theSockets) );
          theCopyStreamService.execute(  new CopyStream(mySocketToNextPeer.getInputStream(), theInSocket.getOutputStream(), theSockets) );
        }
      }catch(Exception e){
        theInSocket.close();
        mySocketToNextPeer.close();
      }
    }
  }

  private class CopyStream implements Runnable{
    private InputStream myInputStream = null;
    private OutputStream myOutputStream = null;
    private SocketProxy[] mySockets = null;

    public CopyStream(InputStream anInputStream, OutputStream anOutputStream, SocketProxy[] aSockets){
      myInputStream = anInputStream;
      myOutputStream = anOutputStream;
      mySockets = aSockets;
    }

    public void run(){
      try {
        IOTools.copyStream( myInputStream, myOutputStream );
      } catch ( IOException e ) {}

      //when the copying of the streams is interrupted, close all sockets to end the socket stream
      for(SocketProxy theSocket : mySockets){
        theSocket.close();
      }
    }
  }
}
