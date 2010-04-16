
package chabernac.queue;

import chabernac.log.Logger;

public class TriggeredQueuePrinter{
  private TriggeredQueueReader printer = null;

  public TriggeredQueuePrinter(TriggeringQueue aQueue) {
    printer = new TriggeredQueueReader(aQueue, 1, new ObjectPrinter());
    printer.setThread(1);
  }
  
  private class ObjectPrinter implements iObjectProcessor{
    public void processObject(Object anObject){
      Logger.log(this,"Object: " + anObject);
    }
  }
}
