package chabernac.synchro;

import chabernac.utils.Record;

public abstract class SynchronizedRecord extends Record implements iSynchronizableRecord {
  
  protected SynchronizedRecord(){
    super();
    setField("NULL", 1, NUMERIC);
    setValue("NULL", new byte[]{0x00});
  }
}
