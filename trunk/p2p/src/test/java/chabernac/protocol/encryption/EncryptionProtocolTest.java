package chabernac.protocol.encryption;

import chabernac.protocol.message.Message;
import junit.framework.TestCase;

public class EncryptionProtocolTest extends TestCase {
  public void testEncryptionProtocol() throws EncryptionException{
    EncryptionProtocol theProtocol = new EncryptionProtocol();
    
    Message theMessage = new Message();
    theMessage.setMessage("The quick brown fox jumps easily over the fat and lazy dog");
    
    theProtocol.encryptMessage(theMessage);
    
    assertTrue(theMessage.containsHeader("SECRET_KEY"));
    assertTrue(theMessage.containsHeader("MESSAGE_HASH"));
    assertTrue(theMessage.containsHeader("PUBLIC_KEY_HASH"));

    theProtocol.decrypteMessage(theMessage);
    
    assertEquals("The quick brown fox jumps easily over the fat and lazy dog", theMessage.getMessage());
  }
}
