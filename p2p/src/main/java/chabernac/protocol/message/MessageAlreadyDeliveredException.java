package chabernac.protocol.message;

import chabernac.protocol.message.AbstractMessageProtocol.Response;

public class MessageAlreadyDeliveredException extends MessageException {

  private static final long serialVersionUID = 5745652862949898660L;

  public MessageAlreadyDeliveredException() {
    super();
  }

  public MessageAlreadyDeliveredException(String anMessage,
                                          Response aResponseCode) {
    super(anMessage, aResponseCode);
  }

  public MessageAlreadyDeliveredException(String anMessage, Throwable anCause) {
    super(anMessage, anCause);
  }

  public MessageAlreadyDeliveredException(String anMessage) {
    super(anMessage);
  }

  public MessageAlreadyDeliveredException(Throwable anCause) {
    super(anCause);
  }

}
