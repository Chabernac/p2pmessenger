package chabernac.protocol.asyncfiletransfer;

public interface iTransferChangeListener {
  public void transferStarted(String aTransferId);
  public void transferRemoved(String aTransferId);
}
