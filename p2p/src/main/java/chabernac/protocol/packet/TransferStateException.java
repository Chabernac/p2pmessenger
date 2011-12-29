package chabernac.protocol.packet;

public class TransferStateException extends Exception {

  private static final long serialVersionUID = 1894586073511382047L;

  public TransferStateException() {
    super();
  }

  public TransferStateException(String aMessage, Throwable aCause) {
    super(aMessage, aCause);
  }

  public TransferStateException(String aMessage) {
    super(aMessage);
  }

  public TransferStateException(Throwable aCause) {
    super(aCause);
  }

}
