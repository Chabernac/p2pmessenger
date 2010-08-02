package chabernac.protocol.routing;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.doomdark.uuid.UUID;
import org.doomdark.uuid.UUIDGenerator;

public class UUIDTest extends TestCase{
  public void testUUIDSequence() throws InterruptedException{
    int times = 100;
    List<UUID> theList = new ArrayList<UUID>();
    for(int i=0;i<times;i++){
      theList.add(UUIDGenerator.getInstance().generateTimeBasedUUID());
      Thread.sleep(2);
    }
    for(int i=1;i<theList.size();i++){
      UUID thePrevUUid = theList.get(i-1);
      UUID theUUid = theList.get(i);
      assertTrue(thePrevUUid.compareTo(theUUid) < 0);
    }
  }
}
