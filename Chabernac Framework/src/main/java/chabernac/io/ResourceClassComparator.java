package chabernac.io;

import java.util.Comparator;

public class ResourceClassComparator implements Comparator {

  public int compare(Object anO1, Object anO2) {
    if(anO1 == FileResource.class) return -1;
    if(anO2 == FileResource.class) return 1;
    return anO1.getClass().getName().compareTo(anO2.getClass().getName());
  }

}
