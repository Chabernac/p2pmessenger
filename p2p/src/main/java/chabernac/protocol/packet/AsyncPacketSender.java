package chabernac.protocol.packet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import chabernac.protocol.packet.PacketTransferState.Direction;
import chabernac.protocol.packet.PacketTransferState.State;

public class AsyncPacketSender extends AbstractPacketTransfer {
  private static final Logger LOGGER = Logger.getLogger(AsyncPacketSender.class);
  private final iDataPacketProvider myDataPacketProvider;
  private final String myDestination;
  private final PacketProtocol myPacketProtocol;
  private final String myTransferId;
  private ExecutorService mySenderService = null;
  private boolean isSending = false;
  private boolean isFailed = false;

  public AsyncPacketSender ( PacketProtocol aPacketProtocol, String aTransferId, String aDestination, iDataPacketProvider aPacketProvider) {
    super();
    myDataPacketProvider = aPacketProvider;
    myDestination = aDestination;
    myTransferId = aTransferId;
    myPacketProtocol = aPacketProtocol;
  }

  @Override
  public void start() {
    stop();
    if(mySenderService == null) mySenderService = Executors.newSingleThreadExecutor();

    mySenderService.execute(new Runnable() {
      @Override
      public void run() {
        try{
          while(myDataPacketProvider.hasNextPacket()){
            isSending = true;
            isFailed = false;
            DataPacket thePacket = myDataPacketProvider.getNextPacket();
            Packet theSendPacket = new Packet( myDestination, thePacket.getId(), myTransferId, thePacket.getBytes(), PacketProtocol.MAX_HOP_DISTANCE, false );
            myPacketProtocol.sendPacket(theSendPacket);
          }
        }catch(Exception e){
          isFailed = true;
          LOGGER.error("An error occured while sending async packet", e);
        } finally {
          isSending = false;
        }
      }
    });
  }

  @Override
  public void stop() {
    if(mySenderService != null) {
      mySenderService.shutdownNow();
      mySenderService = null;
    }
  }

  @Override
  public void done() {
    stop();
    try {
      myDataPacketProvider.close();
    } catch (IOException e) {
      LOGGER.error("An error occured while closing data packet provider", e);
    }
  }

  @Override
  public void waitUntillDone() {
  }

  @Override
  public PacketTransferState getTransferState() {
    ArrayList<String> empty = new ArrayList<String>();
    State theState = State.STARTED;
    if(isFailed) theState = State.FAILED;
    if(!isSending) theState = State.STOPPED;
    return new PacketTransferState(myTransferId, empty, empty, empty, myDataPacketProvider.getNrOfPackets(), Direction.SENDING, theState);
  }

}
