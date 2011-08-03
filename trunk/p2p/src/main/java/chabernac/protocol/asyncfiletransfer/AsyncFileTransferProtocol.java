/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.asyncfiletransfer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import chabernac.io.Base64ObjectStringConverter;
import chabernac.io.iObjectStringConverter;
import chabernac.protocol.Protocol;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.asyncfiletransfer.FileTransferState.Direction;
import chabernac.protocol.message.AsyncMessageProcotol;
import chabernac.protocol.message.Message;
import chabernac.protocol.routing.AbstractPeer;
import chabernac.protocol.routing.RoutingProtocol;
import chabernac.protocol.routing.RoutingTable;
import chabernac.protocol.routing.RoutingTableEntry;
import chabernac.thread.DynamicSizeExecutor;

public class AsyncFileTransferProtocol extends Protocol implements iTransferController {
  private static final Logger LOGGER = Logger.getLogger( AsyncFileTransferProtocol.class );

  public static final String ID = "AFP";

  static enum Command{ACCEPT_FILE, ACCEPT_PACKET, END_FILE_TRANSFER, STOP_TRANSFER, RESUME_TRANSFER, TRANSFER_STOPPED, TRANSFER_CANCELLED, CANCEL_TRANSFER};
  static enum Response{FILE_ACCEPTED, FILE_REFUSED, PACKET_OK, PACKET_REFUSED, NOK, UNKNOWN_ID, END_FILE_TRANSFER_OK, ABORT_FILE_TRANSFER, OK, TRANSFER_PENDING};

  int myPacketSize = 8192;
  int myMaxRetries = 8;
  //when this number of failers happen consecutive the file transfer will be aborted to avoid floading the network with unroutable messages
  int myMaxConsecutiveFailures = 10;

  iObjectStringConverter<FilePacket> myObjectPerister = new Base64ObjectStringConverter<FilePacket>();

  //  private Map<String, FilePacketIO> myFilePacketIO = new HashMap<String, FilePacketIO>();

  private final Object LOCK = new Object();
  private Map<String, FileSender> mySendingFiles = new HashMap<String, FileSender>();
  private Map<String, FileReceiver> myReceivingFiles = new HashMap<String, FileReceiver>();

  iAsyncFileTransferHandler myHandler = null;

  ExecutorService myPacketSenderService = new DynamicSizeExecutor(1, 5, 5);
  ExecutorService myFileSenderService = new DynamicSizeExecutor(5, 5, 50);

  private ExecutorService myAcceptFileService = new DynamicSizeExecutor(1,5,5);

  //just for test purposes, if set to a number > 0 everty 1 / myIsIgnorePacketRatio packets will be simulated as being lost so that a resend
  //of the packed will be triggered
  private int myIsIgnorePacketRatio = -1;
  private Random myRandom = new Random();

  private List<iTransferChangeListener> myTransferChangeListeners = new ArrayList<iTransferChangeListener>();

  public AsyncFileTransferProtocol( ) {
    super( ID );
  }

  @Override
  public String getDescription() {
    return "Async file transfer protocol";
  }

  AsyncMessageProcotol getMessageProtocol() throws ProtocolException{
    return (AsyncMessageProcotol)findProtocolContainer().getProtocol( AsyncMessageProcotol.ID);
  }

  RoutingTable getRoutingTable() throws ProtocolException{
    return ((RoutingProtocol)findProtocolContainer().getProtocol( RoutingProtocol.ID)).getRoutingTable();
  }


  @Override
  public String handleCommand( String aSessionId, String anInput ) {

    try{
      if(anInput.startsWith( Command.ACCEPT_FILE.name() )){
        if(myHandler == null) return Response.ABORT_FILE_TRANSFER.name();
        String[] theParams = anInput.substring( Command.ACCEPT_FILE.name().length() + 1 ).split( " " );

        String theFileName = theParams[0];
        String theUUId = theParams[1];
        int thePacketSize = Integer.parseInt( theParams[2] );
        int theNrOfPackets = Integer.parseInt( theParams[3] );
        String thePeerId = theParams[4];

        try{
          myAcceptFileService.submit(new FileAccepter(theFileName, theUUId, thePeerId, thePacketSize, theNrOfPackets));
          return Response.TRANSFER_PENDING.name();
        }catch(RejectedExecutionException e){
          return Response.FILE_REFUSED.name();
        }
      } else if(anInput.startsWith( Command.ACCEPT_PACKET.name() )){
        if(myHandler == null) return Response.ABORT_FILE_TRANSFER.name();
        String thePack = anInput.substring(Command.ACCEPT_PACKET.name().length() + 1 );
        FilePacket thePacket = myObjectPerister.getObject( thePack );

        if(isSimulateLostPacket()) {
          LOGGER.debug("Simulating lost packet '" + thePacket.getPacket() + "'");
          return Response.NOK.name();
        }

        if(!myReceivingFiles.containsKey( thePacket.getId() )){
          return Response.UNKNOWN_ID.name();
        }

        FileReceiver theIO = myReceivingFiles.get(thePacket.getId());
        theIO.writePacket( thePacket );


        myHandler.fileTransfer( theIO.getFile().getName(), thePacket.getId(), theIO.getPercentageComplete());

        LOGGER.debug( "Packet accepted '" + thePacket.getPacket() + "'" );
        return Response.PACKET_OK.name();
      } else if(anInput.startsWith( Command.END_FILE_TRANSFER.name() )){
        if(myHandler == null) return Response.ABORT_FILE_TRANSFER.name();
        String[] theParams = anInput.substring( Command.END_FILE_TRANSFER.name().length() + 1 ).split( " " );

        String theUUId = theParams[0];
        FilePacketIO theIO = myReceivingFiles.get(theUUId).getIO();
        if(theIO.isComplete()){
          myHandler.fileSaved( theIO.getFile() );
          return Response.END_FILE_TRANSFER_OK.name();
        } else {
          StringBuilder theIncompletePackets = new StringBuilder();
          for(int i=0;i<theIO.getWrittenPackets().length;i++){
            if(!theIO.getWrittenPackets()[i]){
              theIncompletePackets.append( i );
              theIncompletePackets.append( " " );
            }
          }
          return theIncompletePackets.toString();
        }
      } else if(anInput.startsWith( Command.STOP_TRANSFER.name() )){
        String[] theParams = anInput.substring( Command.STOP_TRANSFER.name().length() + 1 ).split( " " );
        String theUUId = theParams[0];
        if(!mySendingFiles.containsKey( theUUId )) return Response.UNKNOWN_ID.name();

        iFileIO theSender = mySendingFiles.get(theUUId);
        theSender.stop();
        return Response.OK.name();
      } else if(anInput.startsWith( Command.CANCEL_TRANSFER.name() )){
        String[] theParams = anInput.substring( Command.CANCEL_TRANSFER.name().length() + 1 ).split( " " );
        String theUUId = theParams[0];
        if(!mySendingFiles.containsKey( theUUId )) return Response.UNKNOWN_ID.name();

        iFileIO theSender = mySendingFiles.get(theUUId);
        theSender.cancel();
        return Response.OK.name();
      } else if(anInput.startsWith( Command.TRANSFER_CANCELLED.name() )){
        String[] theParams = anInput.substring( Command.TRANSFER_CANCELLED.name().length() + 1 ).split( " " );
        String theUUId = theParams[0];
        if(!myReceivingFiles.containsKey( theUUId )) return Response.UNKNOWN_ID.name();
        iFileIO  theFileIo = myReceivingFiles.get(theUUId);
        //only when the transfer was not completed
        //otherwise an already completed tranfer would be removed and the user can not know that the transfer was done successfully
        if(!theFileIo.isComplete()){
          myReceivingFiles.remove( theUUId );
          notifyTransferRemoved(theUUId);
        }
      } else if(anInput.startsWith( Command.RESUME_TRANSFER.name() )){
        String[] theParams = anInput.substring( Command.RESUME_TRANSFER.name().length() + 1 ).split( " " );
        String theUUId = theParams[0];
        if(!mySendingFiles.containsKey( theUUId )) return Response.UNKNOWN_ID.name();
        LOGGER.debug( "Resuming file transfer for transfer '" + theUUId + "'" );
        resume( theUUId );
        return Response.OK.name();
      } else if(anInput.startsWith( Command.TRANSFER_STOPPED.name() )){
        String[] theParams = anInput.substring( Command.TRANSFER_STOPPED.name().length() + 1 ).split( " " );
        String theUUId = theParams[0];
        if(!myReceivingFiles.containsKey( theUUId )) return Response.UNKNOWN_ID.name();

        FileReceiver theReceiver = myReceivingFiles.get(theUUId);
        theReceiver.setTransferring( false );
      }

    }catch(Exception e){
      LOGGER.error( "Error occured in ayncfiletransferprotocol", e );
      return Response.NOK.name();
    }

    // TODO Auto-generated method stub
    return null;
  }

  private boolean isSimulateLostPacket() {
    if(myIsIgnorePacketRatio <= 0) return false;
    return (myRandom.nextInt() % myIsIgnorePacketRatio == 0); 
  }

  String sendMessageTo(AbstractPeer aPeer, String aMessage) throws AsyncFileTransferException{
    return sendMessageTo( aPeer, aMessage, 5, TimeUnit.SECONDS );
  }

  String sendMessageTo(AbstractPeer aPeer, String aMessage, int aTimeout, TimeUnit aTimeUnit) throws AsyncFileTransferException{
    try{
      Message theMessage = new Message();
      theMessage.setDestination( aPeer );
      theMessage.setMessage( createMessage( aMessage ));
      theMessage.setProtocolMessage( true );
      return getMessageProtocol().sendAndWaitForResponse( theMessage, 5, TimeUnit.SECONDS );
    }catch(Exception e){
      throw new AsyncFileTransferException("Could not send message", e);
    }
  }

  /**
   * will send a message async to the peer and return the id of the message
   */
  String sendMessageAsyncTo(AbstractPeer aPeer, String aMessage) throws AsyncFileTransferException{
    try{
      Message theMessage = new Message();
      theMessage.setDestination( aPeer );
      theMessage.setMessage( createMessage( aMessage ));
      theMessage.setProtocolMessage( true );
      getMessageProtocol().sendMessage( theMessage );
      return theMessage.getMessageId().toString();
    }catch(Exception e){
      throw new AsyncFileTransferException("Could not send message", e);
    }
  }

  String waitForResponse(String aMessageId, long aTimeout, TimeUnit aTimeUnit) throws AsyncFileTransferException{
    try {
      return getMessageProtocol().getResponse( aMessageId, aTimeout, aTimeUnit );
    } catch ( Exception e ) {
      throw new AsyncFileTransferException("Error occured while waiting for response", e);
    }
  }

  void cancelResponse(String aMessageId) throws AsyncFileTransferException{
    try{
      getMessageProtocol().cancelResponse(aMessageId);
    }catch(Exception e){
      throw new AsyncFileTransferException("could not cancel response", e);
    }
  }

  void testReachable(String aPeer) throws AsyncFileTransferException{
    RoutingTableEntry theEntryForPeer;
    try {
      theEntryForPeer = getRoutingTable().getEntryForPeer( aPeer );
      if(!theEntryForPeer.isReachable()) throw new AsyncFileTransferException("The peer became you want to send a file to is unreachable");
    } catch (Exception e) {
      throw new AsyncFileTransferException("Not able to retrieve routing table entry for peer '" + aPeer + "'");
    }
  }

  public FileTransferHandler sendFile(final File aFile, final String aPeer){
    //create a new FilePacketIO for this file transfer
    FilePacketIO theIO = FilePacketIO.createForRead( aFile, myPacketSize );
    //store it
    FileSender theFileSender = new FileSender(aPeer, theIO, this);
    mySendingFiles.put(theIO.getId(), theFileSender);
    notifyNewTransfer(theIO.getId());
    theFileSender.startAsync( myFileSenderService );

    return new FileTransferHandler(theIO.getId(), this);
  }

  public iAsyncFileTransferHandler getHandler() {
    return myHandler;
  }

  public void setFileHandler( iAsyncFileTransferHandler aHandler ) {
    myHandler = aHandler;
  }

  int getIsIgnorePacketRatio() {
    return myIsIgnorePacketRatio;
  }

  void setIsIgnorePacketRatio( int aIsIgnorePacketRatio ) {
    myIsIgnorePacketRatio = aIsIgnorePacketRatio;
  }

  protected int getPacketSize() {
    return myPacketSize;
  }

  protected void setPacketSize( int aPacketSize ) {
    myPacketSize = aPacketSize;
  }

  protected int getMaxRetries() {
    return myMaxRetries;
  }

  protected void setMaxRetries( int aMaxRetries ) {
    myMaxRetries = aMaxRetries;
  }

  @Override
  public void stop() {
    for(FileSender theSender : mySendingFiles.values()){
      theSender.stop();
    }
    for(FileReceiver theReceiver : myReceivingFiles.values()){
      theReceiver.stop();
    }
  }

  @Override
  public void cancel(String aTransferId) throws AsyncFileTransferException {
    iFileIO theIO = getFileIO( aTransferId );
    theIO.cancel();
    removeFileIO( aTransferId );
  }

  @Override
  public void pause(String aTransferId) throws AsyncFileTransferException {
    iFileIO theIO = getFileIO( aTransferId );
    theIO.stop();
  }

  @Override
  public FileTransferHandler resume(final String aTransferId) throws AsyncFileTransferException {
    synchronized(LOCK){
      iFileIO theFileIo = getFileIO( aTransferId );
      theFileIo.startAsync( myFileSenderService );
    }

    return new FileTransferHandler(aTransferId, this);
  }

  @Override
  public FileTransferState getState(String aTransferId){
    synchronized(LOCK){
      if(!containsTransferId( aTransferId )) {
        return new FileTransferState(new Percentage( 0, 0 ), FileTransferState.State.CANCELLED_OR_REMOVED, Direction.UNKNOWN, null);
      }

      try{
        Direction theDirection = mySendingFiles.containsKey(aTransferId) ? Direction.SENDING : Direction.RECEIVING;
        iFileIO theFileIO = getFileIO( aTransferId );
        if(theFileIO.isComplete())  {
          return new FileTransferState(theFileIO.getPercentageComplete(), FileTransferState.State.DONE, theDirection, theFileIO.getCompletedPackets());
        } else if(theFileIO.isTransferring()){
          return new FileTransferState(theFileIO.getPercentageComplete(), FileTransferState.State.RUNNING, theDirection, theFileIO.getCompletedPackets());
        } else if(theFileIO.isFailed()){
          return new FileTransferState(theFileIO.getPercentageComplete(), FileTransferState.State.FAILED, theDirection, theFileIO.getCompletedPackets());
        } else if(theFileIO.isPaused()){
          return new FileTransferState(theFileIO.getPercentageComplete(), FileTransferState.State.PAUSED, theDirection, theFileIO.getCompletedPackets());
        } else if(theFileIO.isRefused()){
          return new FileTransferState(new Percentage( 0, 0 ), FileTransferState.State.REFUSED, theDirection, null);
        } else {
          return new FileTransferState(new Percentage( 0, 0 ), FileTransferState.State.NOT_STARTED, theDirection, null);
        }
      }catch(Exception e){
        LOGGER.error("We should not come here", e);
        return null;
      }
    }
  }

  @Override
  public void removeFinished() {
    synchronized(LOCK){
      for(Iterator<String> theTransferIds = mySendingFiles.keySet().iterator();theTransferIds.hasNext();){
        String theTransferId = theTransferIds.next();
        FileSender theSender = mySendingFiles.get(theTransferId);
        if(theSender.isComplete()){
          mySendingFiles.remove(theTransferId);
          notifyTransferRemoved(theTransferId);
        }
      }
      for(Iterator<String> theTransferIds = myReceivingFiles.keySet().iterator();theTransferIds.hasNext();){
        String theTransferId = theTransferIds.next();
        FileReceiver theReceiver = myReceivingFiles.get(theTransferId);
        if(theReceiver.isComplete()){
          myReceivingFiles.remove(theTransferId);
          notifyTransferRemoved(theTransferId);
        }
      }
    }
  }

  @Override
  public Set<String> getSendingTransfers() {
    return mySendingFiles.keySet();
  }

  @Override
  public Set<String> getReceivingTransfers() {
    return myReceivingFiles.keySet();
  }

  @Override
  public FileTransferHandler getTransferHandler( String aTransferId ) throws AsyncFileTransferException {
    if(myReceivingFiles.containsKey( aTransferId )) return new FileTransferHandler( aTransferId, this);
    if(mySendingFiles.containsKey( aTransferId )) return new FileTransferHandler( aTransferId, this);
    throw new AsyncFileTransferException("No handler for transfer id '" + aTransferId + "'");
  }

  private iFileIO getFileIO( String aTransferId ) throws AsyncFileTransferException {
    synchronized(LOCK){
      if(myReceivingFiles.containsKey( aTransferId )) return myReceivingFiles.get( aTransferId );
      if(mySendingFiles.containsKey( aTransferId )) return mySendingFiles.get(aTransferId);
      throw new AsyncFileTransferException("No file io for transfer id '" + aTransferId + "'");
    }
  }

  private void removeFileIO(String aTransferId){
    synchronized(LOCK){
      if(myReceivingFiles.containsKey( aTransferId )) myReceivingFiles.remove( aTransferId );
      if(mySendingFiles.containsKey( aTransferId )) mySendingFiles.remove(aTransferId);
      notifyTransferRemoved(aTransferId);
    }
  }

  @Override
  public void waitUntillDone( String aTransferId ) throws AsyncFileTransferException {
    getFileIO( aTransferId ).waitTillDone();
  }

  public boolean containsTransferId(String aTransferId){
    if(myReceivingFiles.containsKey( aTransferId )) return true;
    if(mySendingFiles.containsKey( aTransferId )) return true;
    return false;
  }

  @Override
  public File getFile(String anTransferId) throws AsyncFileTransferException {
    return getFileIO(anTransferId).getFile();
  }

  @Override
  public void addFileTransferListener( String aTransferId, iFileTransferListener aListener ) throws AsyncFileTransferException {
    getFileIO( aTransferId ).addFileTransferListener(aListener);
  }

  @Override
  public void addTransferChangeListener(iTransferChangeListener aListener) {
    myTransferChangeListeners.add(aListener);
  }

  private void notifyNewTransfer(String aTransferId){
    for(iTransferChangeListener theListener : myTransferChangeListeners) theListener.transferStarted(aTransferId);
  }

  private void notifyTransferRemoved(String aTransferId){
    for(iTransferChangeListener theListener : myTransferChangeListeners) theListener.transferRemoved(aTransferId);
  }

  private class FileAccepter implements Runnable{
    private final String myFileName;
    private final String myUUId;
    private final String myPeer;
    private final int myPacketSize;
    private final int myNrOfPackets;

    public FileAccepter(String anFileName, String anId, String anPeer,
        int anPacketSize, int anNrOfPackets) {
      super();
      myFileName = anFileName;
      myUUId = anId;
      myPeer = anPeer;
      myPacketSize = anPacketSize;
      myNrOfPackets = anNrOfPackets;
    }

    public void run(){
      try{
        testReachable(myPeer);
        AbstractPeer theSender = getRoutingTable().getEntryForPeer(myPeer).getPeer();

        File theFile = myHandler.acceptFile( myFileName, myUUId );


        if(theFile == null){
          sendMessageAsyncTo(theSender, Command.CANCEL_TRANSFER.name() + " " + myUUId);
        } else {

          FilePacketIO theIO = FilePacketIO.createForWrite( theFile, myUUId, myPacketSize, myNrOfPackets );

          if(!myReceivingFiles.containsKey(myUUId)){
            myReceivingFiles.put( myUUId, new FileReceiver( myPeer, theIO, AsyncFileTransferProtocol.this) );
            notifyNewTransfer(myUUId);
          }

          myReceivingFiles.get(myUUId).setTransferring( true );

          //send a message to the sender that it can start sending
          sendMessageAsyncTo(theSender, Command.RESUME_TRANSFER.name() + " " + myUUId);
        }
      }catch(Exception e){
        LOGGER.error("Error occured in FileAccepter", e);
      }

    }
  }
}
