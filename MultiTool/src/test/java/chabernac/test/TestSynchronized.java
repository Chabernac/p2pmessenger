package chabernac.test;

public class TestSynchronized {
  
  public synchronized void a(){
    b();
  }
  
  private synchronized void b(){
    System.out.println("b");
  }
  

  public static void main(String[] args) {
    new TestSynchronized().a();

  }

}
