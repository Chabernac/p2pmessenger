package chabernac.queue;

import chabernac.utils.Debug;

public class CycleQueue extends Queue
{
	public CycleQueue(String queueName)
	{
		super(queueName);
	}

	public Object get()
	{
		try
		{
			if(getEnabled && putEnabled)
			{
				Object o = super.get();
				put(o);
				return o;
			}
			else
			{
				return null;
			}
		}catch(Exception e)
		{
			Debug.log(this,"Exception occured in get of cycleQueue",e);
			return null;
		}
	}
}