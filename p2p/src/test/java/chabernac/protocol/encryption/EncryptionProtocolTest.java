package chabernac.protocol.encryption;

import chabernac.protocol.message.Message;
import chabernac.protocol.routing.DummyPeer;
import junit.framework.TestCase;

public class EncryptionProtocolTest extends TestCase {

//  static{
//    BasicConfigurator.resetConfiguration();
//    BasicConfigurator.configure();
//  }

  public void testEncryptionProtocol() throws EncryptionException{
    EncryptionProtocol theSendingProtocol = new EncryptionProtocol();
    EncryptionProtocol theReceivingProtocol = new EncryptionProtocol();

    theSendingProtocol.setPublicKeyFor("2", theReceivingProtocol.getPublicKey());
    theReceivingProtocol.setPublicKeyFor( "3", theSendingProtocol.getPublicKey() );

    Message theMessage = new Message();
    theMessage.setDestination(new DummyPeer("2"));
    theMessage.setSource( new DummyPeer( "3" ) );
    theMessage.setMessage("The quick brown fox jumps easily over the fat and lazy dog");

    theSendingProtocol.encryptMessage(theMessage);

    assertTrue(theMessage.containsHeader("SECRET_KEY"));
    assertTrue(theMessage.containsHeader("MESSAGE_HASH"));
    assertTrue(theMessage.containsHeader("PUBLIC_KEY_HASH"));

    theReceivingProtocol.decryptMessage(theMessage);

    assertEquals("The quick brown fox jumps easily over the fat and lazy dog", theMessage.getMessage());
  }

  public void testStressEncryption() throws EncryptionException, InterruptedException{
    EncryptionProtocol theSendingProtocol = new EncryptionProtocol();
    EncryptionProtocol theReceivingProtocol = new EncryptionProtocol();
    theSendingProtocol.setPublicKeyFor("2", theReceivingProtocol.getPublicKey());
    theReceivingProtocol.setPublicKeyFor( "3", theSendingProtocol.getPublicKey() );

    long t1 = System.currentTimeMillis();
    int times = 100;
    for(int i=0;i<times;i++){

      Message theMessage = new Message();
      theMessage.setDestination(new DummyPeer("2"));
      theMessage.setSource(new DummyPeer("3"));
      theMessage.setMessage("The quick brown fox jumps easily over the fat and lazy dog");

      theSendingProtocol.encryptMessage(theMessage);
      theReceivingProtocol.decryptMessage(theMessage);

      assertEquals("The quick brown fox jumps easily over the fat and lazy dog", theMessage.getMessage());
    }

    long t2 = System.currentTimeMillis();

    long deltatT = t2 - t1;

    double theTimesMinute = (double)(times * 1000 * 60) / (double)deltatT;

    System.out.println("Encrypt/decriptions per minute: '" + theTimesMinute + "'");

    //TODO not so performant, search for a more performant way of doing encryption decryption
    assertTrue(theTimesMinute > 1000);
  }
}
