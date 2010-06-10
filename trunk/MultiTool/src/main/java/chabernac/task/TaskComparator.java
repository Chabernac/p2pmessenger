package chabernac.task;

import java.util.Comparator;

public class TaskComparator implements Comparator {

  public int compare(Object o1, Object o2) {
    Task theTask1 = (Task)o1;
    Task theTask2 = (Task)o2;
    return theTask1.getSequenceIndicator() - theTask2.getSequenceIndicator();
  }

}
