/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.android.drinklist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.database.DataSetObservable;

public class DrinkList extends DataSetObservable{
   private List<DrinkOrder> myOrders = new ArrayList<DrinkOrder>();
   
   public void addDrink(DrinkOrder aDrinkOrder){
     if(!myOrders.contains( aDrinkOrder )){
       myOrders.add(aDrinkOrder);
     } else {
       myOrders.get(myOrders.indexOf( aDrinkOrder )).increment( 1 );
     }
     
     notifyChanged();
     System.out.println("Drink added '" + aDrinkOrder.getDrink().getName() + "'");
   }
   
   public void removeAll(DrinkOrder aDrinkOrder){
     myOrders.remove( aDrinkOrder );
     notifyChanged();
   }
   
   public void removeDrink(DrinkOrder aDrinkOrder){
     if(!myOrders.contains( aDrinkOrder )) return;
     
     DrinkOrder theOrder = myOrders.get(myOrders.indexOf( aDrinkOrder )); 
     theOrder.decrease( 1 );
     if(theOrder.getNumberOfDrinks() == 0) myOrders.remove( theOrder );
     
     notifyChanged();
   }
   
   public List<DrinkOrder> getList(){
     return Collections.unmodifiableList( myOrders );
   }
   
   public int getDrinkOrder(Drink aDrink){
     int theCounter = 0;
     for(DrinkOrder theDrinkOrder : myOrders){
       if(theDrinkOrder.getDrink().equals( aDrink )){
         theCounter += theDrinkOrder.getNumberOfDrinks();
       }
     }
     return theCounter;
   }
   
   public DrinkOrder getDrinkAt(int anIndex){
     return myOrders.get(anIndex);
   }
   
   public String toString(){
     String s = "";
     for(DrinkOrder theDrink : myOrders){
       s += theDrink.getNumberOfDrinks() + " x " + theDrink.getDrink().getName() + "\r\n"; 
     }
     return s;
   }

  public void clear() {
    myOrders.clear();
    notifyChanged();
  }
  
  public int size(){
    return myOrders.size();
  }

  public float getTotal(iPriceProvider aPriceProvider) {
    float theTotal = 0;
    for(DrinkOrder theOrder : myOrders){
      theTotal += aPriceProvider.getPrice( theOrder.getDrink() ) * theOrder.getNumberOfDrinks();
    }
    return theTotal;
  }

}
