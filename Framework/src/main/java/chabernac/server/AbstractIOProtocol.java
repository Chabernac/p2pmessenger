
package chabernac.server;

import java.io.IOException;
import java.net.Socket;

import chabernac.log.Logger;

public abstract class AbstractIOProtocol implements iProtocol {

  public void handle(Socket aSocket) {
    try {
      Channel theChannel = new Channel(aSocket.getInputStream(), aSocket.getOutputStream());
      handle(theChannel);
      theChannel.close();
    } catch (IOException e) {
      Logger.log(this,"Could not create channel", e);
    }
    
  }
  
  protected abstract void handle(Channel aChannel);

}
