/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.utils;

import java.lang.reflect.Array;

public class ArrayTools {
  public static Object growArray(Object a, int grow)
  {
    Class cl = a.getClass();
    if(!cl.isArray()) return null;;
    Class componentType = a.getClass().getComponentType();
    int length = Array.getLength(a);
    int newLength = length + grow;
    Object newArray = Array.newInstance(componentType,newLength);
    System.arraycopy(a,0,newArray,0,length);
    return newArray;
  }
}
