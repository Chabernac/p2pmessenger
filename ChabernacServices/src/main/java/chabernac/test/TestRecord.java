package chabernac.test;

import chabernac.synchro.SynchronizedEvent;
import chabernac.synchro.SynchronizedRecord;

public class TestRecord {
  public static void main(String args[]){
    R001 theRecord = new R001();
    theRecord.setValue("X", -5.1245);
    System.out.println(theRecord.getDoubleValue("X"));
    System.out.println(new String(theRecord.getContent()));
  }
  
  private static class R001 extends SynchronizedRecord{
    public R001(){
      setField("X", 6, NUMERIC_SIGNED, 2);
    }

    public SynchronizedEvent getEvent() {
      // TODO Auto-generated method stub
      return null;
    }
    
  }
}
