package chabernac.queue;

import java.util.Vector;
import chabernac.utils.*;

public class BlockQueuer
{
  private Class listType = null;
  private Vector list = null;
  private int submitSize;
  private Queue putQueue = null;

  public BlockQueuer(Queue putQueue, Class listType, int submitSize)
  {
    this.putQueue = putQueue;
    this.listType = listType;
    this.submitSize = submitSize;
    initialize();
  }

  private void initialize()
  {
    try
    {
    list = (Vector)listType.newInstance();
    }catch(Exception e)
      {
        Debug.log(this,"List of type: " + listType.toString() + " could not be instantiated",e);
      }
  }

  public synchronized void put(Object o)
  {
	//Debug.log(this,"Object put on Blockqueuer");
    if(list!=null)
    {
      list.addElement(o);
      //Debug.log(this,"List size: " + list.size());
      if(list.size() >= submitSize)
      {
		   putOnQueue();
      }
    }
    else
    {
      Debug.log(this,"Could not add element, list is not instantiated");
    }
  }

  public synchronized void putOnQueue()
  {
	  try
	  {
	  	if(putQueue.isPutEnabled() && list.size() > 0)
	  	{
			Debug.log(this,"List put on queue: " + list.size());
	  		putQueue.put(list);
	   		list = (Vector)listType.newInstance();
	  	}
	  }catch(Exception e)
	      {
	              Debug.log(this,"List of type: " + listType.toString() + " could not be instantiated",e);
          }
  }

public void setSubmitSize(int submitSize){this.submitSize = submitSize;}
public void setListType(Class listType){this.listType = listType;}
public void setPutQueue(Queue putQueue){this.putQueue = putQueue;}
public int getSubmitSize(){return submitSize;}
public Class getListType(){return listType;}
public Queue getPutQueue(){return putQueue;}
public int waitSize(){return list.size();}


}