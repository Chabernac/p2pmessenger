package chabernac.protocol.routing;

import java.util.Comparator;

public class SuperNodeSorter implements Comparator<String> {

  @Override
  public int compare(String aNode1, String aNode2) {
    if(aNode1.toLowerCase().startsWith("http")){
      return -1;
    } else if(aNode2.toLowerCase().startsWith("http")){
      return 1;
    }
    return aNode1.compareTo(aNode2);
  }
}
