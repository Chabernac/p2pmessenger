package chabernac.protocol.filetransfer;

import chabernac.protocol.Protocol;

public class FileTransferProtocol extends Protocol {

  public FileTransferProtocol() {
    super("FTP");
  }

  @Override
  public String getDescription() {
    return "File Transfer Protocol";
  }

  @Override
  protected String handleCommand(long aSessionId, String anInput) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected void stopProtocol() {
    // TODO Auto-generated method stub

  }

}
