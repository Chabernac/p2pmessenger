package chabernac.protocol.asyncfiletransfer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import chabernac.protocol.asyncfiletransfer.AsyncFileTransferProtocol.Command;
import chabernac.protocol.asyncfiletransfer.AsyncFileTransferProtocol.Response;
import chabernac.protocol.message.Message;
import chabernac.protocol.routing.AbstractPeer;

/**
 * PacketSender is a helper class for AsyncFileTransferProtocol
 * It used to be an inner class of AsyncFileTransferProtocol but made a seperate class of it to keep AsyncFileTransferProtocol clean
 * It uses protected methods and variables of AsyncFileTransferProtocol
 *
 */
public class PacketSender {
  private static final Logger LOGGER = Logger.getLogger( AsyncFileTransferProtocol.class );
  //just for testing and debugging
  static int SEND_SLEEP = -1;

  private CountDownLatch myLatch;
  private final AtomicInteger myFailuresLeft;
  private final FilePacketIO myFilePacketIO;
  private final FileSender myFileIO;
  private final AbstractPeer myDestination;
  private final AtomicBoolean myIsContinue = new AtomicBoolean(true);
  private final AsyncFileTransferProtocol myProtocol;
  private final boolean[] mySendPackets;

  public PacketSender(FileSender aFileIO, FilePacketIO anIO, AbstractPeer aDestination, AsyncFileTransferProtocol aProtocol, boolean[] aSendPackets){
    myFileIO = aFileIO;
    myFilePacketIO = anIO;
    myLatch = new CountDownLatch(myFilePacketIO.getNrOfPackets());
    myDestination = aDestination;
    myProtocol = aProtocol;
    mySendPackets = aSendPackets;
    myFailuresLeft = new AtomicInteger(myProtocol.myMaxConsecutiveFailures);
  }

  public boolean isContinue(){
    return myIsContinue.get();
  }

  public void setContinue(boolean isContinue){
    myIsContinue.set(isContinue);
  }

  public boolean waitUntillAllSend() throws InterruptedException{
    myLatch.await(5, TimeUnit.SECONDS);
    return myLatch.getCount() == 0;
  }

  public void resetLatch(int aNumberOfPacketsLeft){
    myLatch = new CountDownLatch(aNumberOfPacketsLeft);
  }

  public void sendPacket(final int aPacketNr) throws AsyncFileTransferException{
    //test if the peer is still reachable
    myProtocol.testReachable(myDestination.getPeerId());

    //change and use ArrayBlockingQueue or something like that
    myProtocol.myPacketSenderService.execute( new Runnable(){
      public void run(){
        try{
          FilePacket thePacket = myFilePacketIO.getPacket(aPacketNr);
          Message theMessage = new Message();
          theMessage.setDestination( myDestination );
          theMessage.setProtocolMessage( true );
          theMessage.setMessage( myProtocol.createMessage( Command.ACCEPT_PACKET + " " + myProtocol.myObjectPerister.toString( thePacket ) ) );
          LOGGER.debug("Packet send '" + aPacketNr + "'");
          String theResponse = myProtocol.getMessageProtocol().sendAndWaitForResponse( theMessage, 5, TimeUnit.SECONDS );
          if(theResponse.startsWith( Response.PACKET_REFUSED.name() )){
            //do not continue if the packet was refused
            myIsContinue.set( false );
          } else if(theResponse.startsWith(Response.PACKET_OK.name())){
            //reset the failures left counter to the max
            myFailuresLeft.set(myProtocol.myMaxConsecutiveFailures);
            mySendPackets[aPacketNr] = true;
            myFileIO.calculatePercentageComplete();
            myFileIO.notifyListeners();
          }
          if(SEND_SLEEP > 0) Thread.sleep( SEND_SLEEP );
        }catch(Exception e){
          LOGGER.error("Error occured while sending packet " + aPacketNr, e);
          if(myFailuresLeft.decrementAndGet() <= 0){
            LOGGER.error("Max number of failures reached, aborting file transfer");
            myIsContinue.set( false );
          }
        }finally {
          myLatch.countDown();
        }
      }
    });
  }
}
