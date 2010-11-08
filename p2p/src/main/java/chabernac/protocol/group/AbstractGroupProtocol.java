package chabernac.protocol.group;

import chabernac.protocol.Protocol;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.message.MessageException;
import chabernac.protocol.message.MultiPeerMessageProtocol;

public abstract class AbstractGroupProtocol extends Protocol {
  private final ProtocolContainer myContainer;
  private final Group myGroup;
  private final MultiPeerMessageProtocol myMessageProtocol;

  public AbstractGroupProtocol(ProtocolContainer aContainer, Group aGroup, String anId) throws ProtocolException {
    super(anId);
    myContainer = aContainer;
    myGroup = aGroup;
    myMessageProtocol = (MultiPeerMessageProtocol)aContainer.getProtocol(MultiPeerMessageProtocol.ID);
  }

  @Override
  public void stop() {
   myContainer.removeProtocol(this);
  }
  
  public void register(){
    myContainer.addProtocol(this);
  }
  
  public void sendMessageToGroup(String aMessage) throws MessageException{
   myMessageProtocol.sendMessage(myGroup.createEmptyMessage().setMessage(createMessage(aMessage)));
  }

}