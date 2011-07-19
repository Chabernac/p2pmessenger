package chabernac.protocol.asyncfiletransfer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FileTransferHandler {
  private final Future<Boolean> isTransferred;
  private final String myTransferId;
  private final iTransferController myTransferController;
  
  

  public FileTransferHandler(Future<Boolean> anIsTransferred,
      String anTransferId, iTransferController anTransferController) {
    super();
    isTransferred = anIsTransferred;
    myTransferId = anTransferId;
    myTransferController = anTransferController;
  }

  public boolean waitForTransferred() throws InterruptedException, ExecutionException{
    return isTransferred.get();
  }

  public String getTransferId(){
    return myTransferId;
  }

  public void interrupt() throws AsyncFileTransferException{
    myTransferController.removeAndInterrupt(myTransferId);
  }

  public void pause() throws AsyncFileTransferException{
    myTransferController.pause(myTransferId);
  }

  public void resume() throws AsyncFileTransferException{
    myTransferController.resume(myTransferId);
  }

  public FileTransferState getState() throws AsyncFileTransferException{
    return myTransferController.getState(myTransferId);
  }
}
