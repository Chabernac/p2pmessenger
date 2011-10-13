package chabernac.protocol.packet;



public class PacketTransferHandler {
  private final String myTransferId;
  private final iPacketTransferController myTransferController;

  public PacketTransferHandler(String anTransferId, iPacketTransferController anTransferController) {
    super();
    myTransferId = anTransferId;
    myTransferController = anTransferController;
  }

  public String getTransferId(){
    return myTransferId;
  }
  
  public void start() throws StateChangeException{
   myTransferController.start( myTransferId ); 
  }
  
  public void stop() throws StateChangeException{
   myTransferController.stop( myTransferId ) ; 
  }
  
  public void addPacketTransferListener(iPacketTransferListener aPacketTransferListener){
    myTransferController.addPacketTransferListener( myTransferId, aPacketTransferListener );
  }
  
  public void waitUntillDone(){
    myTransferController.waitUntillDone( myTransferId );
  }
}
