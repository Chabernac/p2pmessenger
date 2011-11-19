package chabernac.protocol.group;

import chabernac.protocol.Protocol;
import chabernac.protocol.ProtocolContainer;
import chabernac.protocol.ProtocolException;
import chabernac.protocol.message.Message;
import chabernac.protocol.message.MessageException;
import chabernac.protocol.message.MessageProtocol;
import chabernac.protocol.routing.AbstractPeer;

public abstract class AbstractGroupProtocol extends Protocol {
  private final ProtocolContainer myContainer;
  private final Group myGroup;
  private final MessageProtocol myMessageProtocol;

  public AbstractGroupProtocol(ProtocolContainer aContainer, Group aGroup, String anId) throws ProtocolException {
    super(anId);
    myContainer = aContainer;
    myGroup = aGroup;
    myMessageProtocol = (MessageProtocol)aContainer.getProtocol(MessageProtocol.ID);
  }

  @Override
  public void stop() {
   myContainer.removeProtocol(this);
  }
  
  public void register(){
    myContainer.addProtocol(this);
  }
  
  public void sendMessageToGroup(String aMessage) throws MessageException{
    for(AbstractPeer thePeer : myGroup.getMembers()){
      sendMessageToPeer( thePeer, aMessage );
    }
  }
  
  public void sendMessageToPeer(AbstractPeer aPeer, String aMessage) throws MessageException{
    Message theMessage = new Message();
    theMessage.setProtocolMessage( true );
    theMessage.setDestination( aPeer );
    theMessage.setMessage( createMessage( aMessage ));
    myMessageProtocol.sendMessage(theMessage);
  }

}