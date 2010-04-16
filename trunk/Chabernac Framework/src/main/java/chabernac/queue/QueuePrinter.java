package chabernac.queue;

/**
This class reads object from a Queue and than prints them.
*/

public class QueuePrinter extends QueueObserver
{
  public QueuePrinter(Queue aQueue)
  {
    super(aQueue);
  }

  public void processObject(Object o)
  {
    System.out.println("Object: " + o.toString());
  }
}