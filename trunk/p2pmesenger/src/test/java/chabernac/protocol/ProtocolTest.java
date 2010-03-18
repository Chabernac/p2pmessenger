package chabernac.protocol;

import junit.framework.TestCase;
import chabernac.protocol.ping.PingProtocol;

public class ProtocolTest extends TestCase {
  public void testCreateMessage(){
    ProtocolContainer theProtocol = new ProtocolContainer();
    PingProtocol thePingProtocol = new PingProtocol();
//    PingProtocol thePingProtocol2 = new PingProtocol();
    
    theProtocol.addProtocol(thePingProtocol);
//    thePingProtocol.addProtocol(thePingProtocol2);
    String theMessage = thePingProtocol.createMessage("test");
    
    //because the message created in the second ping protocol
    //the own protocol prefix, the parent protocol prefix and master protocol prefix will be added 
    //to the message.
    assertEquals("PPGtest", theMessage);
  }
  
  public void testGetProtocolsString(){
    ProtocolContainer theProtocol = new ProtocolContainer();
    PingProtocol thePingProtocol = new PingProtocol();
//    PingProtocol thePingProtocol2 = new PingProtocol();
    
    theProtocol.addProtocol(thePingProtocol);
//    thePingProtocol.addProtocol(thePingProtocol2);
//    theProtocol.addProtocol(new MasterProtocol());
    
    
    assertEquals("PPG;MAS;", theProtocol.getProtocolString());
  }
}
