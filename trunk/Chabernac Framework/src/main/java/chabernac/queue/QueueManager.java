package chabernac.queue;

import java.util.Vector;

public class QueueManager
{
  private Vector myQueues = null;

  public QueueManager()
  {
    initialize();
  }

private void initialize()
{
  myQueues = new Vector(2,2);
}

public void addQueue(Queue aQueue)
{
  myQueues.addElement(aQueue);
}
public void removeQueueAt(int i)
{
  myQueues.removeElementAt(i);
}
public Queue queueAt(int i)
{
  if(i>=0 && i<myQueues.size())
  {
    return (Queue)myQueues.elementAt(i);
  }
  else return null;
}
public Queue getQueue(String id)
{
  return queueAt(searchQueue(id));
}
public void deleteQueue(String id)
{
  myQueues.removeElementAt(searchQueue(id));
}

private int searchQueue(String id) {
  Queue theQueue = null;
  for(int i=0;i<myQueues.size();i++)
  {
    theQueue = (Queue)myQueues.elementAt(i);
    if(theQueue.getId().equals(id))
    {
      return i;
    }
  }
  return -1;
}



}