package chabernac.protocol.packet;


public interface iPacketTransferController {
  public void start(String aTransferId);
  public void stop(String aTransferId);
  public void addPacketTransferListener(String aTransferId, iPacketTransferListener aPacketTransferListener);
  public void waitUntillDone(String aTransferId);
}
