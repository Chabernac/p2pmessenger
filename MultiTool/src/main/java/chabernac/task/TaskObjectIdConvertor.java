package chabernac.task;

import chabernac.object.iObjectIdConvertor;

public class TaskObjectIdConvertor implements iObjectIdConvertor {

  public int getObjectId(Object anObject) {
    if(anObject instanceof Task){
      return ((Task)anObject).getFullName().intern().hashCode();
    } else {
      return anObject.toString().intern().hashCode();
    }
  }

}
