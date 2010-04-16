package chabernac.queue;

public interface QueueInterface{
	public void put(Object aObject) throws QueueException;
	public Object get() throws QueueException;
	public void setGetEnabled(boolean getEnabled);
	public void setPutEnabled(boolean putEnabled);
	public boolean isPutEnabled();
	public boolean isGetEnabled();
	public int size();
	public void setMaxSize(int aSize);
	public int getMaxSize();
	public void clear();

}