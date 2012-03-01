package chabernac.protocol;

import chabernac.io.iInputOutputHandler;

public class InputOutputProtocolAdapter implements iInputOutputHandler{
  private final IProtocol myProtocol;

  public InputOutputProtocolAdapter(IProtocol myProtocol) {
    super();
    this.myProtocol = myProtocol;
  }

  @Override
  public String handle(String anInput) {
    return myProtocol.handleCommand(null, null, anInput);
  }

  @Override
  public void close() {
    myProtocol.stop();
  }
  
  
}
