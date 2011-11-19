package chabernac.protocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

public class ProtocolSorterTest extends TestCase {
  public void testProtocolSorter(){
    List<IProtocol> myProtocols = new ArrayList<IProtocol>();
    
    myProtocols.add(new ImportanceProtocol(3));
    myProtocols.add(new ImportanceProtocol(1));
    myProtocols.add(new ImportanceProtocol(5));
    myProtocols.add(new ImportanceProtocol(2));
    myProtocols.add(new ImportanceProtocol(10));
    
    Collections.sort(myProtocols, new ProtocolSorter());
    
    assertEquals(10, myProtocols.get(0).getImportance());
    assertEquals(5, myProtocols.get(1).getImportance());
    assertEquals(3, myProtocols.get(2).getImportance());
    assertEquals(2, myProtocols.get(3).getImportance());
    assertEquals(1, myProtocols.get(4).getImportance());
  }
  
  private class ImportanceProtocol implements IProtocol{
    private int myImportance;

    public ImportanceProtocol(int myImportance) {
      super();
      this.myImportance = myImportance;
    }

    @Override
    public String handleCommand(String aSessionId, String anInput) {
      return null;
    }

    @Override
    public String getId() {
      return null;
    }

    @Override
    public void setMasterProtocol(IProtocol aProtocol) {
    }

    @Override
    public void stop() {
    }

    @Override
    public void setServerInfo(ServerInfo aServerInfo) throws ProtocolException {
    }

    @Override
    public int getImportance() {
      return myImportance;
    }
    
  }
  
}
