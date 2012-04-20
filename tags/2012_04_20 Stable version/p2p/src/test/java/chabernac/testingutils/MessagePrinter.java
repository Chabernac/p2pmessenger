package chabernac.testingutils;

import org.apache.log4j.Logger;

import chabernac.protocol.message.MultiPeerMessage;
import chabernac.protocol.message.iMultiPeerMessageListener;

public class MessagePrinter implements iMultiPeerMessageListener {
  private static final Logger LOGGER = Logger.getLogger(MessagePrinter.class);

  @Override
  public void messageReceived(MultiPeerMessage aMessage) {
    LOGGER.debug(aMessage.getMessage());
  }

}
