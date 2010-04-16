package chabernac.queue;

import chabernac.server.*;
import chabernac.utils.Debug;
import java.io.Serializable;

public class SocketPusher extends QueueObserver
{
  private DefaultObjectProtocol protocol = null;

  public SocketPusher(Queue aQueue, DefaultObjectProtocol protocol)
  {
    super(aQueue);
    this.protocol = protocol;
  }

  public void processObject(Object o)
  {
    if(o instanceof Serializable)
    {
      try
      {
		protocol.send("PUT");
        protocol.send(o);
        //Thread.sleep(100);
      }catch(Exception e){Debug.log(this,"Could not push object",e);}
    }
    else
    {
      Debug.log(this,"Not a serializable object: " + o.toString());
    }
  }
}