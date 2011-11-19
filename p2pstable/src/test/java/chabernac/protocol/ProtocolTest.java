package chabernac.protocol;

import junit.framework.TestCase;
import chabernac.protocol.ping.PingProtocol;
import chabernac.tools.PropertyMap;

public class ProtocolTest extends TestCase {
  public void testCreateMessage(){
    PingProtocol thePingProtocol = new PingProtocol();
//    PingProtocol thePingProtocol2 = new PingProtocol();
    
//    thePingProtocol.addProtocol(thePingProtocol2);
    String theMessage = thePingProtocol.createMessage("test");
    
    //because the message created in the second ping protocol
    //the own protocol prefix, the parent protocol prefix and master protocol prefix will be added 
    //to the message.
    assertEquals("PPGtest", theMessage);
  }
  
  public void testGetProtocolsString() throws ProtocolException{
    ProtocolContainer theMasterProtocol = new ProtocolContainer(new ProtocolFactory(new PropertyMap()));
    
    theMasterProtocol.getProtocol( PingProtocol.ID );
    
    
    assertEquals("PPG;MAS;", theMasterProtocol.getProtocolString());
  }
}
