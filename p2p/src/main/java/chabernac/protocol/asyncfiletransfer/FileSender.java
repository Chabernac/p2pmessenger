package chabernac.protocol.asyncfiletransfer;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import chabernac.protocol.asyncfiletransfer.AsyncFileTransferProtocol.Command;
import chabernac.protocol.asyncfiletransfer.AsyncFileTransferProtocol.Response;
import chabernac.protocol.routing.AbstractPeer;

public class FileSender extends AbstractFileIO{
  private static final Logger LOGGER = Logger.getLogger( FileSender.class );

  private final String myPeer;
  private final AsyncFileTransferProtocol myProtocol;
  private final FilePacketIO myFilePacketIO;
  private PacketSender myPacketSender;
  private int myLastPacketSend = -1;
  private boolean isSending = false;
  private boolean isComplete = false;
  private Percentage myPercentageCompleted = new Percentage( 0, 0 );
  private ArrayBlockingQueue<Boolean> myEventQueue = new ArrayBlockingQueue<Boolean>( 1 );
  private Future<Boolean> myTransferComplete;
  private final boolean[] mySendPackets;

  public FileSender(String anPeer, FilePacketIO anIo, AsyncFileTransferProtocol anProtocol) {
    super();
    myPeer = anPeer;
    myProtocol = anProtocol;
    myFilePacketIO = anIo;
    mySendPackets = new boolean[anIo.getNrOfPackets()];
  }


  public void startAsync(ExecutorService aService){
    Exception e = new Exception();
    e.fillInStackTrace();
    LOGGER.error( "Starting file sender ascyn", e );
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

  public void start() throws AsyncFileTransferException{
    AbstractPeer theDestination = null;
    try{
      synchronized(this){
        if(isSending) throw new AsyncFileTransferException("Already in sending state");
        isSending = true;
      }

      myProtocol.testReachable(myPeer); 
      theDestination = myProtocol.getRoutingTable().getEntryForPeer(myPeer).getPeer();

      //init file transfer with other peer
      String theResult = myProtocol.sendMessageTo( theDestination, Command.ACCEPT_FILE.name() + " " + 
          myFilePacketIO.getFile().getName()  + " " + 
          myFilePacketIO.getId() + " " + 
          myFilePacketIO.getPacketSize() + " " + 
          myFilePacketIO.getNrOfPackets() + " " + 
          myProtocol.getRoutingTable().getLocalPeerId());

      //only continue if the file was accepted by the client
      if(!theResult.startsWith( Response.FILE_ACCEPTED.name() )) throw new AsyncFileTransferException("Transferring file aborted");

      myPacketSender = new PacketSender(this, myFilePacketIO, theDestination, myProtocol, mySendPackets);
      //now loop over all packets and send them to the other peer
      while(myLastPacketSend++ < myFilePacketIO.getNrOfPackets() && myPacketSender.isContinue()){
        myPacketSender.sendPacket(myLastPacketSend);

        if(myProtocol.myHandler != null) {
          myProtocol.myHandler.fileTransfer( myFilePacketIO.getFile().getName(), myFilePacketIO.getId(), myPercentageCompleted);
        }
      }

      if(!myPacketSender.isContinue()) throw new AsyncFileTransferException("Transferring file aborted because a packet was refused");

      myPacketSender.waitUntillAllSend();

      String theResponse = null;
      int j=0;
      while( j++ < myProtocol.myMaxRetries && !(theResponse = myProtocol.sendMessageTo( theDestination, Command.END_FILE_TRANSFER.name()  + " " + myFilePacketIO.getId())).equalsIgnoreCase(Response.END_FILE_TRANSFER_OK.name())){
        //if we get here not all packets where correctly delivered resend the missed packets
        LOGGER.debug("Some packets need to be resended '" + theResponse + "'");
        String[] thePacketsToResend = theResponse.split(" ");
        myPacketSender.resetLatch(thePacketsToResend.length);
        for(int i=0;i<thePacketsToResend.length && myPacketSender.isContinue();i++){
          LOGGER.debug("Resending packet '" + thePacketsToResend[i] + "'");
          myPacketSender.sendPacket(Integer.parseInt(thePacketsToResend[i]));
        }

        if(!myPacketSender.isContinue()) throw new AsyncFileTransferException("Transferring file aborted because a packet was refused");

        myPacketSender.waitUntillAllSend();
      }

      if(!theResponse.equalsIgnoreCase(Response.END_FILE_TRANSFER_OK.name())){
        throw new AsyncFileTransferException("Transferring file failed, not all packets where send successfull, missing packet number '" + theResponse + "'");
      }

      isComplete = true;
    }catch(Exception e){
      if(e instanceof AsyncFileTransferException) throw (AsyncFileTransferException)e;
      throw new AsyncFileTransferException("Could not send file to peer '" + myPeer + "'", e);
    } finally {
      if(theDestination != null){
        myProtocol.sendMessageTo( theDestination, Command.TRANSFER_STOPPED + " " + myFilePacketIO.getId() );
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
    waitTillDone();
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
}
