package chabernac.protocol.asyncfiletransfer;

import java.io.File;
import java.util.Set;

public interface iTransferController {
  //return a set of transfers id's for wich the controller has state
  public Set<String> getSendingTransfers();
  
  //return a set of transfers id's for wich the controller has state
  public Set<String> getReceivingTransfers();

  //get the transfer handler for this id
//if the transfer with this id does not exist throw an exception
  public FileTransferHandler getTransferHandler(String aTransferId) throws AsyncFileTransferException;
  
  //resume the transfer with the given id and return a new transfer handler
  //if the transfer with this id does not exist throw an exception
  public FileTransferHandler resume(String aTransferId) throws AsyncFileTransferException;
  
  //interrupt the tranfer with the given id
  //if the transfer with this id does not exist throw an exception
  //if the transfer is running then stop it
  public void cancel(String aTransferId) throws AsyncFileTransferException;
  
  //pause the transfer with the given id
  //if the transfer with this id does not exist throw an exception
  public void pause(String aTransferId) throws AsyncFileTransferException;
  
  //wait untill the given transfer has finished
  //if the transfer with this id does not exist throw an exception
  public void waitUntillDone(String aTransferId) throws AsyncFileTransferException;
  
  //return the state of the transfer
  //if the transfer with this id does not exist a state CANCELLED_OR_REMOVED is returned
  public FileTransferState getState(String anTransferId);
  
  //remove the status of all finished file transfers
  public void removeFinished();

  //return the file of this transfer
  //if the transfer with this id does not exist throw an exception
  public File getFile(String anTransferId) throws AsyncFileTransferException;

  //add a file transfer listener for this transfer
  //if the transfer with this id does not exist throw an exception
  public void addFileTransferListener( String aTransferId, iFileTransferListener aListener ) throws AsyncFileTransferException;
}
