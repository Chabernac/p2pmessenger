package chabernac.webcam;

public class WCException extends Exception{
  private static final long serialVersionUID = 7772937587746300040L;

  public WCException() {
    super();
  }

  public WCException(String anArg0, Throwable anArg1) {
    super(anArg0, anArg1);
  }

  public WCException(String anArg0) {
    super(anArg0);
  }

  public WCException(Throwable anArg0) {
    super(anArg0);
  }
}
