package chabernac.protocol.asyncfiletransfer;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import chabernac.protocol.ProtocolException;
import chabernac.protocol.asyncfiletransfer.AsyncFileTransferProtocol.Command;
import chabernac.protocol.asyncfiletransfer.AsyncFileTransferProtocol.Response;
import chabernac.protocol.routing.AbstractPeer;
import chabernac.protocol.routing.UnknownPeerException;

public class FileSender extends AbstractFileIO{
  private static final Logger LOGGER = Logger.getLogger( FileSender.class );

  private final String myPeer;
  private final AsyncFileTransferProtocol myProtocol;
  private final FilePacketIO myFilePacketIO;
  private PacketSender myPacketSender;
  private int myLastPacketSend = -1;
  private boolean isSending = false;
  private boolean isComplete = false;
  private boolean isRefused = false;
  private boolean isFailed = false;
  private boolean isPending = false;
  private Percentage myPercentageCompleted = new Percentage( 0, 0 );
  private ArrayBlockingQueue<Boolean> myEventQueue = new ArrayBlockingQueue<Boolean>( 1 );
  private Future<Boolean> myTransferComplete;
  private final boolean[] mySendPackets;
  private String myPendingMessage = null;

  public FileSender(String anPeer, FilePacketIO anIo, AsyncFileTransferProtocol anProtocol) {
    super();
    myPeer = anPeer;
    myProtocol = anProtocol;
    myFilePacketIO = anIo;
    mySendPackets = new boolean[anIo.getNrOfPackets()];
  }


  public void startAsync(ExecutorService aService){
    LOGGER.debug("Sheduling file transfer '" + myFilePacketIO.getId() + "'");
    myTransferComplete = aService.submit( new Callable<Boolean>(){
      @Override
      public Boolean call() throws Exception {
        try{
          start();
          return Boolean.TRUE;
        }catch(Exception e){
          LOGGER.error("An error occured while starting file sender asynchronous", e);
          return Boolean.FALSE;
        }
      }
    });
  }

  void calculatePercentageComplete(){
    int theSucc = 0;
    for(boolean ok: mySendPackets){
      if(ok) theSucc++;
    }
    myPercentageCompleted = new Percentage( theSucc, mySendPackets.length );
  }
  
  private AbstractPeer getDestination() throws UnknownPeerException, ProtocolException{
    return myProtocol.getRoutingTable().getEntryForPeer(myPeer).getPeer();
  }

  public void start() throws AsyncFileTransferException{
    LOGGER.debug("Staring file transfer '" + myFilePacketIO.getId() + "'");
    AbstractPeer theDestination = null;
    try{
      synchronized(this){
        if(isSending) throw new AsyncFileTransferException("Already in sending state");
        isSending = true;
        isFailed = false;
        isPending = false;
      }

      myProtocol.testReachable(myPeer);
      theDestination = getDestination();

      //init file transfer with other peer
      myPendingMessage = myProtocol.sendMessageAsyncTo( theDestination, Command.ACCEPT_FILE.name() + ";" + 
          myFilePacketIO.getFile().getName()  + ";" + 
          myFilePacketIO.getId() + ";" + 
          myFilePacketIO.getPacketSize() + ";" + 
          myFilePacketIO.getNrOfPackets() + ";" + 
          myProtocol.getRoutingTable().getLocalPeerId());
      
      isPending = true;
      notifyListeners();

      String theResult = myProtocol.waitForResponse( myPendingMessage, 5, TimeUnit.MINUTES);
      isPending = Response.TRANSFER_PENDING.name().equalsIgnoreCase(theResult);
      notifyListeners();
      if(isPending) return;
      
      isRefused = Response.FILE_REFUSED.name().equals( theResult );
      
      //only continue if the file was accepted by the client
      if(!theResult.startsWith( Response.FILE_ACCEPTED.name() )) throw new AsyncFileTransferException("Transferring file aborted, client did not accept file '" + theResult + "'");

      myPacketSender = new PacketSender(this, myFilePacketIO, theDestination, myProtocol, mySendPackets);
      //now loop over all packets and send them to the other peer
      while(++myLastPacketSend < myFilePacketIO.getNrOfPackets() && myPacketSender.isContinue()){
        LOGGER.debug("Queing packet for send of file transfer '" + myFilePacketIO.getId() + " packet: '" + myLastPacketSend + "'");
        myPacketSender.sendPacket(myLastPacketSend);

        if(myProtocol.myHandler != null) {
          myProtocol.myHandler.fileTransfer( myFilePacketIO.getFile().getName(), myFilePacketIO.getId(), myPercentageCompleted);
        }
      }

      if(!myPacketSender.isContinue() && myPacketSender.isErrorOccured()) throw new AsyncFileTransferException("Transferring file aborted because a packet was refused");
      if(!myPacketSender.isContinue()) return;

      myPacketSender.waitUntillAllSend();

      String theResponse = null;
      int j=0;
      while( j++ < myProtocol.myMaxRetries && !(theResponse = myProtocol.sendMessageTo( theDestination, Command.END_FILE_TRANSFER.name()  + ";" + myFilePacketIO.getId())).equalsIgnoreCase(Response.END_FILE_TRANSFER_OK.name())){
        //if we get here not all packets where correctly delivered resend the missed packets
        LOGGER.debug("Some packets need to be resended '" + theResponse + "'");
        String[] thePacketsToResend = theResponse.split(";");
        myPacketSender.resetLatch(thePacketsToResend.length);
        for(int i=0;i<thePacketsToResend.length && myPacketSender.isContinue();i++){
          LOGGER.debug("Resending packet '" + thePacketsToResend[i] + "'");
          myPacketSender.sendPacket(Integer.parseInt(thePacketsToResend[i]));
        }

        if(!myPacketSender.isContinue() && myPacketSender.isErrorOccured()) throw new AsyncFileTransferException("Transferring file aborted because a packet was refused");
        if(!myPacketSender.isContinue()) return;

        myPacketSender.waitUntillAllSend();
      }

      if(!theResponse.equalsIgnoreCase(Response.END_FILE_TRANSFER_OK.name())){
        throw new AsyncFileTransferException("Transferring file failed, not all packets where send successfull, missing packet number '" + theResponse + "'");
      }

      isComplete = true;
    }catch(Exception e){
      isFailed = true;
      if(e instanceof AsyncFileTransferException) throw (AsyncFileTransferException)e;
      throw new AsyncFileTransferException("Could not send file to peer '" + myPeer + "'", e);
    } finally {
      if(theDestination != null){
        try{
          myProtocol.sendMessageTo( theDestination, Command.TRANSFER_STOPPED + ";" + myFilePacketIO.getId() );
        }catch(AsyncFileTransferException e){
          LOGGER.error("Could not send '" + Command.TRANSFER_STOPPED.name() + "' to peer '" + myPeer + "'");
        }
      }
      //always close the file when we are not sending any more to free resources
      try {
        myFilePacketIO.close();
      } catch ( IOException e ) {
        LOGGER.error("Could not close file packet io", e);
      }
      synchronized(this){
        isSending = false;
        notifyAll();
      }
      notifyListeners();
    }
  }

  public void stop(){
    if(myPacketSender != null){
      myPacketSender.setContinue(false);
    }
    if(myPendingMessage != null){
      try {
        myProtocol.cancelResponse( myPendingMessage );
      } catch ( AsyncFileTransferException e ) {
        LOGGER.error("Unable to cancel pending message", e);
      }
    }
    waitTillDone();
  }
  
  public void cancel(){
    stop();
    //send signal to the receiver that the transfer will be removed and can not be resumed
    try {
      String theResponse = myProtocol.sendMessageTo( getDestination(), Command.TRANSFER_CANCELLED.name() + ";" + myFilePacketIO.getId() );
      LOGGER.debug("Response on cancellation '" + theResponse + "'");
    } catch ( Exception e ) {
      LOGGER.error( "Unable to notify receiver of transfer cancellation", e );
    }
  }

  public void reset(){
    myLastPacketSend = -1;
  }

  public boolean isTransferring(){
    return isSending;
  }

  public boolean isComplete(){
    return isComplete;
  }

  public Percentage getPercentageComplete() {
    return myPercentageCompleted;
  }

  @Override
  public void waitTillDone() {
    try {
      myTransferComplete.get();
    } catch ( Exception e ) {
      LOGGER.error("Error occured whild waiting untill done", e);
    }
  }


  @Override
  public boolean[] getCompletedPackets() {
    return mySendPackets;
  }


  @Override
  public File getFile() {
    return myFilePacketIO.getFile();
  }


  @Override
  public boolean isRefused() {
    return isRefused;
  }


  @Override
  public boolean isFailed() {
    return isFailed;
  }


  @Override
  public boolean isPending() {
    return isPending;
  }
}
