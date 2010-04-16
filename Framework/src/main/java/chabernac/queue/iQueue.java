
package chabernac.queue;

public interface iQueue {
  public Object get();
  public void put(Object anObject);
  public int size();
}
