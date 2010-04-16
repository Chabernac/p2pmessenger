package chabernac.queue;

import java.util.Vector;

public class SmartQueue extends Queue
{
	Vector myList = null;

	public SmartQueue(String id)
	{
		super(id);
		initialize();
	}

private void initialize()
{
	myList = new Vector(10,10);
}

 public void putSmart(Comparable aObject) throws QueueException
 {
	 if(!find(aObject))
	 {
		 myList.addElement(aObject);
		 //Debug.log(this,"Putting object on queue");
		 put(aObject);
	 }
 }

 public int totalSize(){return myList.size();}

private boolean find(Comparable aComparable)
{
Comparable theComparable = null;
for(int i=0;i<myList.size();i++)
 	{
		theComparable = (Comparable)myList.elementAt(i);
		if(aComparable.compareTo(theComparable) == 0)
		{
			return true;
		}
	}
return false;
}
public void clearAll()
{
	clear();
	myList.clear();
}

}
