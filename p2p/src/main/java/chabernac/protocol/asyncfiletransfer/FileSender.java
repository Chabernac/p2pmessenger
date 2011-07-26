package chabernac.protocol.asyncfiletransfer;

import org.apache.log4j.Logger;

import chabernac.protocol.asyncfiletransfer.AsyncFileTransferProtocol.Command;
import chabernac.protocol.asyncfiletransfer.AsyncFileTransferProtocol.Response;
import chabernac.protocol.routing.AbstractPeer;

public class FileSender implements iFileIO{
  private static final Logger LOGGER = Logger.getLogger( AsyncFileTransferProtocol.class );
  
  private final String myPeer;
  private final AsyncFileTransferProtocol myProtocol;
  private final FilePacketIO myFilePacketIO;
  private PacketSender myPacketSender;
  private int myLastPacketSend = -1;
  private boolean isSending = false;
  private boolean isComplete = false;
  private double myPercentageCompleted = 0D;
  
  public FileSender(String anPeer, FilePacketIO anIo, AsyncFileTransferProtocol anProtocol) {
    super();
    myPeer = anPeer;
    myProtocol = anProtocol;
    myFilePacketIO = anIo;
  }



  public void start() throws AsyncFileTransferException{
    try{
      synchronized(this){
        if(isSending) throw new AsyncFileTransferException("Already in sending state");
        isSending = true;
      }
      
      myProtocol.testReachable(myPeer); 
      AbstractPeer theDestination = myProtocol.getRoutingTable().getEntryForPeer(myPeer).getPeer();
      
      //init file transfer with other peer
      String theResult = myProtocol.sendMessageTo( theDestination, Command.ACCEPT_FILE.name() + " " + 
          myFilePacketIO.getFile().getName()  + " " + 
          myFilePacketIO.getId() + " " + 
          myFilePacketIO.getPacketSize() + " " + 
          myFilePacketIO.getNrOfPackets());

      //only continue if the file was accepted by the client
      if(!theResult.startsWith( Response.FILE_ACCEPTED.name() )) throw new AsyncFileTransferException("Transferring file aborted");

      myPacketSender = new PacketSender(myFilePacketIO, theDestination, myProtocol);
      //now loop over all packets and send them to the other peer
      while(myLastPacketSend++ < myFilePacketIO.getNrOfPackets() && myPacketSender.isContinue()){
        myPacketSender.sendPacket(myLastPacketSend);
        
        if(myProtocol.myHandler != null) {
          myPercentageCompleted = (double)myLastPacketSend / (double)myFilePacketIO.getNrOfPackets();
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
      synchronized(this){
        isSending = false;
        notifyAll();
      }
    }
  }
  
  public void stop(){
    myPacketSender.setContinue(false);
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

  public double getPercentageComplete() {
    return myPercentageCompleted;
  }

  @Override
  public void waitTillDone() {
    synchronized(this){
      while(isSending){
        try {
          wait();
        } catch (InterruptedException e) {
          LOGGER.error("Could not wait");
        }
      }
    }
  }
}
