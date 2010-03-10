package chabernac.protocol;

import junit.framework.TestCase;
import chabernac.protocol.ping.PingProtocol;

public class ProtocolTest extends TestCase {
  public void testCreateMessage(){
    MasterProtocol theProtocol = new MasterProtocol();
    PingProtocol thePingProtocol = new PingProtocol();
    PingProtocol thePingProtocol2 = new PingProtocol();
    
    theProtocol.addSubProtocol(thePingProtocol);
    thePingProtocol.addSubProtocol(thePingProtocol2);
    String theMessage = thePingProtocol2.createMessage("test");
    
    //because the message created in the second ping protocol
    //the own protocol prefix, the parent protocol prefix and master protocol prefix will be added 
    //to the message.
    assertEquals("MASPPGPPGtest", theMessage);
  }
  
  public void testGetProtocolsString(){
    MasterProtocol theProtocol = new MasterProtocol();
    PingProtocol thePingProtocol = new PingProtocol();
    PingProtocol thePingProtocol2 = new PingProtocol();
    
    theProtocol.addSubProtocol(thePingProtocol);
    thePingProtocol.addSubProtocol(thePingProtocol2);
    theProtocol.addSubProtocol(new MasterProtocol());
    
    
    assertEquals("MAS{MAS;PPG{PPG}}", theProtocol.getProtocolsString());
  }
}
