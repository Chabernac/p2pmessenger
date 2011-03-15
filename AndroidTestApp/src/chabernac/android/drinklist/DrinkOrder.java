/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.android.drinklist;

import java.io.Serializable;

public class DrinkOrder implements Comparable<DrinkOrder>, Serializable{
  private static final long serialVersionUID = 3635698949483331927L;
  
  private final Drink myDrink;
  private final String myOption;
  private int myNumberOfDrinks = 1;
  private final String myName;
  private final String myFullName;
  
  public DrinkOrder( Drink aDrink ){
    myDrink = aDrink;
    myName = myDrink.getName();
    myFullName = myDrink.getDrinkType() + " " + myDrink.getName();
    myOption = null;
  }
  
  public DrinkOrder( Drink aDrink, String aOption ) {
    myDrink = aDrink;
    myOption = aOption;
    myName = myDrink.getName() + " " + myOption;
    myFullName = myDrink.getDrinkType() + " " + myDrink.getName() + " " + myOption;
  }

  public int getNumberOfDrinks() {
    return myNumberOfDrinks;
  }

  public void setNumberOfDrinks( int aNumberOfDrinks ) {
    myNumberOfDrinks = aNumberOfDrinks;
  }
  
  public void increment(int aNumber){
    myNumberOfDrinks += aNumber;
  }
  
  public void decrease(int aNumber){
    myNumberOfDrinks -= aNumber;
    if(myNumberOfDrinks < 0) myNumberOfDrinks = 0;
  }

  public Drink getDrink() {
    return myDrink;
  }

  public String getOption() {
    return myOption;
  }
  
  public int hashCode(){
    return myDrink.getName().hashCode();
  }
  
  public boolean equals(Object anObject){
    if(!(anObject instanceof DrinkOrder)) return false;
    DrinkOrder theDrink = (DrinkOrder)anObject;
    
    if(!myName.equals( theDrink.getName() )) return false;
    return true;
  }

  public String getName() {
    return myName;
  }
  
  public String getFullName(){
    return myFullName;
  }

  @Override
  public int compareTo( DrinkOrder aDrinkOrder ) {
    return getFullName().compareTo( aDrinkOrder.getFullName() );
  }
}
