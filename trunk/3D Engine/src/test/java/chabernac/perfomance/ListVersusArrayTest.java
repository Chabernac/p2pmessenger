/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.perfomance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

public class ListVersusArrayTest extends TestCase {
  public void testListVersusArray(){
    int count = 1000000;
    int elements = 1000;
    
    
    List<String> theList  = new ArrayList<String>();
    for(int i=0;i<elements;i++){
      theList.add("Element" + i);
    }
    
    long t1,t2;
    
    t1 = System.currentTimeMillis();
    for(int i=0;i<count;i++){
      for(String theString : theList){
        theString.length();
      }
    }
    t2 = System.currentTimeMillis();
    
    System.out.println("Nr of string loops per second with list: " + count / (t2-t1));
    
    t1 = System.currentTimeMillis();
    for(int i=0;i<count;i++){
      Iterator<String> theIterator = theList.iterator();
      while(theIterator.hasNext()){
        theIterator.next().length();
      }
    }
    t2 = System.currentTimeMillis();
    
    System.out.println("Nr of string loops per second with iterator on list: " + count / (t2-t1));
    
    t1 = System.currentTimeMillis();
    for(int i=0;i<count;i++){
      for(int j=0;j<theList.size();j++){
        theList.get( j ).length();
      }
    }
    t2 = System.currentTimeMillis();
    
    System.out.println("Nr of string loops per second with list classic: " + count / (t2-t1));
    
    String[] theArray = new String[elements];
    for(int i=0;i<elements;i++){
      theArray[i] = "Element" + i;
    }
    
    t1 = System.currentTimeMillis();
    for(int i=0;i<count;i++){
      for(String theString : theArray){
        theString.length();
      }
    }
    t2 = System.currentTimeMillis();
    
    System.out.println("Nr of string loops per second with array: " + count / (t2-t1));

    t1 = System.currentTimeMillis();
    for(int i=0;i<count;i++){
      for(int j=0;j<elements;j++){
        theArray[j].length();
      }
    }
    t2 = System.currentTimeMillis();
    
    System.out.println("Nr of string loops per second with array classic: " + count / (t2-t1));
    
    t1 = System.currentTimeMillis();
    for(int i=0;i<count;i++){
      int j = 0;
      while(j<elements){
        theArray[j++].length();
      }
    }
    t2 = System.currentTimeMillis();
    
    System.out.println("Nr of string loops per second with while: " + count / (t2-t1));
    
    t1 = System.currentTimeMillis();
    int j;
    for(int i=0;i<count;i++){
      j = 0;
      do{
        theArray[j].length();
      }while(++j<elements);
    }
    t2 = System.currentTimeMillis();
    
    System.out.println("Nr of string loops per second with do while: " + count / (t2-t1));
    
    
  }

}
