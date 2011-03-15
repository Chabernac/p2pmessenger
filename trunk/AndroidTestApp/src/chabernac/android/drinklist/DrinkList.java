/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.android.drinklist;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import android.database.DataSetObservable;
import chabernac.android.tools.IndexSet;

public class DrinkList extends DataSetObservable implements Serializable{
  private static final long serialVersionUID = 8330707881295412881L;
  private List<DrinkOrder> myOrders = new IndexSet<DrinkOrder>();

  public DrinkList(){
  }

  public void addDrink(DrinkOrder aDrinkOrder){
    DrinkOrder theOrder = getDrinkOrder( aDrinkOrder );
    if(theOrder == null){
      myOrders.add(aDrinkOrder);
    } else {
      theOrder.increment( 1 );
    }
    
    notifyChanged();
  }
  
  public void setDrinkOrder(DrinkOrder aDrinkOrder){
    myOrders.add( aDrinkOrder );
  }

  private DrinkOrder getDrinkOrder(DrinkOrder anOrder){
    for(DrinkOrder theOrder : myOrders){
      if(anOrder.equals( theOrder )) return theOrder;
    }
    return null;
  }

  public void removeAll(DrinkOrder aDrinkOrder){
    myOrders.remove( aDrinkOrder );
    notifyChanged();
  }

  public void removeDrink(DrinkOrder aDrinkOrder){
    if(!myOrders.contains( aDrinkOrder )) return;

    DrinkOrder theOrder = getDrinkOrder( aDrinkOrder );
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
  
  public void replace(DrinkList aList){
    myOrders.clear();
    myOrders.addAll( aList.getList() );
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
