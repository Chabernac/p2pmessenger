package chabernac.queue;

import java.util.Observable;
import java.util.Vector;
import java.io.*;
import chabernac.utils.*;

public class Queue extends Observable implements Serializable
{
  protected Vector queue = null;
  protected Vector writeQueue = null;
  private String id = "";
  protected boolean putEnabled = true;
  protected boolean getEnabled = true;
  private int getDisabledNr = -1;
  private int maxQueueSize = -1;
  private int offset = -1;
  private boolean writeToFile = false;
  private int objectsOnDisk = 0;
  private int filesWritten = 0;
  private int filesRead = 0;
  private int lockSize = -1;
  private Queue outputQueue = null;

  public Queue(String id)
  {
    this.id = id;
    initialize();
  }

  private void initialize()
  {
    queue = new Vector(10,10);
    writeQueue = new Vector(10,10);
  }

  public void setGetDisabled(int getDisabledNr){this.getDisabledNr = getDisabledNr;}
  public void setPutEnabled(boolean putEnabled){this.putEnabled = putEnabled;}
  public void setGetEnabled(boolean getEnabled){this.getEnabled = getEnabled;}
  public void setWriteToFile(boolean writeToFile){this.writeToFile = writeToFile;}
  public void setMaxQueueSize(int maxQueueSize){this.maxQueueSize = maxQueueSize;}
  public void setOffset(int offset){this.offset = offset;}
  public boolean isPutEnabled(){return putEnabled;}
  public boolean isGetEnabled(){return getEnabled;}
  public void setOutputQueue(Queue outputQueue){this.outputQueue = outputQueue;}
  public Queue getOutputQueue(){return outputQueue;}
  public void setLockSize(int lockSize){this.lockSize = lockSize;}
  public int getLockSize(){return lockSize;}
  public synchronized void trigger()
  {
	//Debug.log(this,"BEFORE trigger");
	setChanged();
    notifyObservers();
    //Debug.log(this,"AFTER trigger");
  }


  public synchronized void put(Object o) throws QueueException
  {
	//Debug.log(this,"BEFORE put");
	while(lockSize!=-1 && queue.size() >= lockSize)
	{
		try
		{
			wait();
		}catch(Exception e){Debug.log(this,"Waiting interrupted");}
	}
	if(!putEnabled)
	{
		//Debug.log(this,"1");
		throw new QueueException("Put for this queue is disabled");
	}
	else
	{
		//Debug.log(this,"2");
		////Debug.log(this,"Putting object on queue:" + queue.size() + ":" + writeQueue.size() + ":" + objectsOnDisk);
		if(writeToFile && queue.size() + writeQueue.size() + objectsOnDisk >= maxQueueSize)
		{
			writeQueue.addElement(o);
			if(writeQueue.size() >= offset){writeQueue();}
		}
		else
		{
			//Debug.log(this,"3");
    		queue.addElement(o);
    		//Debug.log(this,"4");
		}
		notifyAll();
		//Debug.log(this,"5");
		setChanged();
		//Debug.log(this,"6");
    	notifyObservers();
    	//Debug.log(this,"7");
	}
	//Debug.log(this,"AFTER put");
  }

  public synchronized Object get() throws QueueException
  {
	  if(queue.size() <= 0)
	  {
		try
		{
			wait();
		}catch(Exception e){Debug.log(this,"Waiting interrupted");}
	  }
	  //Debug.log(this,"BEFORE get");
	  Object o = get(0);
	  notifyAll();
	  //Debug.log(this,"AFTER get");
	  if(outputQueue!=null && outputQueue.isPutEnabled()){outputQueue.put(o);}
	  return o;
  }

  public synchronized Object get(int nr) throws QueueException
  {
	if(!getEnabled){throw new QueueException("Get for this queue is disabled");}
	if(nr >= size())
	{
		//throw new QueueException("Queue out of bounds exception")
		return null;
	}
	else
	{
	  if(!isEmpty())
	  {
		if(getDisabledNr>-1)
		{
			if(getDisabledNr--==0){getEnabled=false;}
		}
    	Object o = (Object)queue.elementAt(nr);
    	queue.removeElementAt(nr);
    	if(queue.size() < maxQueueSize - offset && (objectsOnDisk > 0 || writeQueue.size() > 0))
    	{
			readQueue();
		}
		return o;
	  }
	  else
	  {
		  return null;
	  }
    }
  }
  public synchronized int size(){return queue.size() + writeQueue.size() + objectsOnDisk;}
  public void setId(String id){this.id = id;}
  public String getId(){return id;}
  public synchronized boolean isEmpty()
  {
	//Debug.log(this,"BEFORE isEmpty");
	if(queue.size() + writeQueue.size() + objectsOnDisk<=0)
	{
		//Debug.log(this,"AFTER isEmpty");
		return true;
	}
	else
	{
		//Debug.log(this,"AFTER isEmpty");
		return false;
	}
  }
  public void clear()
  {
	  //Debug.log(this,"BEFORE clear");
	  queue.clear();
	  //Debug.log(this,"AFTER clear");
  }

  private void writeQueue()
  {
	  File file = null;
	  ObjectOutputStream objectOutputStream = null;
	  try
	  {
		file = new File("Queue_" + id + "_" + filesWritten + ".obj");
	  	objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
	  	for(int i=0;i<writeQueue.size();i++)
	  	{
			  objectOutputStream.writeObject(writeQueue.elementAt(i));
			  objectsOnDisk++;
	  	}
	  	filesWritten++;
	  	writeQueue.clear();
	  	//Debug.log(this,"Queue : " + id + " written to file:  " + file.toString());
	}catch(Exception e){Debug.log(this,"Could not write objects to file: " + file.toString(),e);}
	 finally
	 {
		 if(objectOutputStream!=null)
		 {
			 try
			 {
		  	 objectOutputStream.flush();
	  	  	 objectOutputStream.close();
		 	 }catch(Exception e){Debug.log(this,"Could not close objectOutputStream",e);}
		 }
	 }

  }

  private void readQueue()
  {
	  if(objectsOnDisk == 0)
	  {
		  pumpWriteToQueue();
	  }
	  else
	  {
		  File file = null;
		  ObjectInputStream objectInputStream = null;
		  try
		  {
			file = new File("Queue_" + id + "_" + filesRead + ".obj");
			objectInputStream = new ObjectInputStream(new FileInputStream(file));
			Object o = null;
			while((o = objectInputStream.readObject())!=null)
			{
				//Debug.log(this,"Reading object from: " + file.toString());
				queue.addElement(o);
				objectsOnDisk--;
			}
		  }catch(EOFException e)
			{
				try
				{
				objectInputStream.close();
				file.delete();
				filesRead++;
				}catch(Exception f){Debug.log(this,"Could not close objectinputstream: " + file.toString(),f);}
			}
		  catch(Exception e){Debug.log(this,"Could not read objects from file: " + file.toString(),e);}
		  finally
		  {
			  if(objectInputStream!=null)
			  {
				  try
				  {
					objectInputStream.close();
				  }catch(Exception e){Debug.log(this,"Could not close objectInputStream",e);}
			  }
		  }
  	  }

  }

  //pump over the write queue to the queue
  private void pumpWriteToQueue()
  {
	while(writeQueue.size() > 0)
	{
		queue.addElement(writeQueue.elementAt(0));
		writeQueue.removeElementAt(0);
	}
  }
}