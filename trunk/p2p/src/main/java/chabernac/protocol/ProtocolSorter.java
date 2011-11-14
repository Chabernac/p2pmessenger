package chabernac.protocol;

import java.util.Comparator;

public class ProtocolSorter implements Comparator<IProtocol> {

  @Override
  public int compare(IProtocol aProtocol1, IProtocol aProtocol2) {
    return aProtocol2.getImportance() - aProtocol1.getImportance();
  }

}
