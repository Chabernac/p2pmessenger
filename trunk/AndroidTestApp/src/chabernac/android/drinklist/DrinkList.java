/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.android.drinklist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import android.database.DataSetObservable;

public class DrinkList extends DataSetObservable{
   private LinkedHashMap<Drink, Integer> myList = new LinkedHashMap<Drink, Integer>();
   
   public void addDrink(Drink aDrink){
     if(!myList.containsKey( aDrink )){
       myList.put(aDrink, new Integer( 1 ));
     } else {
       myList.put(aDrink, myList.get( aDrink ) + 1);
     }
     notifyChanged();
     System.out.println("Drink added '" + aDrink.getName() + "'");
   }
   
   public void removeDrink(Drink aDrink){
     if(!myList.containsKey( aDrink )) return;
     
     if(myList.containsKey( aDrink )){
       myList.put(aDrink, myList.get( aDrink ) - 1);
     }
     
     if(myList.get( aDrink ) <= 0){
       myList.remove(aDrink);
     }
     
     notifyChanged();
     System.out.println("Drink removed '" + aDrink.getName() + "'");
   }
   
   public Map<Drink, Integer> getList(){
     return Collections.unmodifiableMap( myList );
   }
   
   public int getDrinkOrder(Drink aDrink){
     if(!myList.containsKey( aDrink )) return 0;
     return myList.get(aDrink);
   }
   
   public Drink getDrinkAt(int anIndex){
     return new ArrayList<Drink>(myList.keySet()).get( anIndex );
   }
   
   public String toString(){
     String s = "";
     for(Drink theDrink : myList.keySet()){
       s += getDrinkOrder( theDrink ) + " x " + theDrink.getName() + "\r\n"; 
     }
     return s;
   }

}
