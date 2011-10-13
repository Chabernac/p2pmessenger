package chabernac.protocol.packet;


public interface iPacketTransferController {
  public void start(String aTransferId) throws StateChangeException;
  public void stop(String aTransferId) throws StateChangeException;
  public void addPacketTransferListener(String aTransferId, iPacketTransferListener aPacketTransferListener);
  public void waitUntillDone(String aTransferId);
}
