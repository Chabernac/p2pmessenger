package chabernac.math;

public class Average {
  private long[] myNumbers;
  private int myCurrent;
  private long myTotal;
  
  public Average(int aSize){
    myNumbers = new long[aSize];
    init();
  }
  
  private void init(){
    fill(0);
  }
  
  public void fill(long aNumber){
    for(int i=0;i<myNumbers.length;i++){
      addNumber(aNumber);
    }
  }
  
  public void addNumber(long aNumber){
    myTotal += aNumber;
    myTotal -= myNumbers[myCurrent];
    myNumbers[myCurrent] = aNumber;
    
    myCurrent = ++myCurrent % myNumbers.length;
  }
  
  public long getTotal(){
    return myTotal;
  }
  
  public double getAverage(){
    return (double)getTotal() / (double)myNumbers.length;
  }
  
  public static void main(String args[]){
    Average theAverage = new Average(3);
    theAverage.addNumber(1);
    theAverage.addNumber(2);
    theAverage.addNumber(3);
    theAverage.addNumber(4);
    theAverage.addNumber(5);
    System.out.println(theAverage.getAverage());
    
  }

}
