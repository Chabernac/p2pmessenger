
package chabernac.server;

import java.io.IOException;

import org.apache.log4j.Logger;

public abstract class AbstractIOProtocol implements iProtocol {
  private static Logger logger = Logger.getLogger(AbstractIOProtocol.class);

  public void handle(SocketDecorator aSocket) {
    try {
      Channel theChannel = new Channel(aSocket.getInputStream(), aSocket.getOutputStream());
      handle(theChannel);
      theChannel.close();
    } catch (IOException e) {
      logger.error("Could not create channel", e);
    }
    
  }
  
  protected abstract void handle(Channel aChannel);

}
