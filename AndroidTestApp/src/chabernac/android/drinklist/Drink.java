/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.android.drinklist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Drink {
  private final String myDrinkType;
  private final String myName;
  private final int myImageResource;
  private List<String> myDrinkOptions = new ArrayList<String>();

  public Drink( String aDrinkType, String aName, int aImageResource ) {
    super();
    myDrinkType = aDrinkType;
    myName = aName.replace( '_', ' ' );
    myImageResource = aImageResource;
  }
  public String getName() {
    return myName;
  }
  
  public String getDrinkType() {
    return myDrinkType;
  }
  public int getImageResource() {
    return myImageResource;
  }
  
  public List<String> getSubSelections(){
    return Collections.unmodifiableList( myDrinkOptions );
  }
  
  public void addDrinkOption(String aDrinkOption){
    myDrinkOptions.add(aDrinkOption);
  }
  
  public boolean equals(Object anObject){
    if(!(anObject instanceof Drink)) return false;
    
    Drink theDrink = (Drink)anObject;
    if(!theDrink.getName().equals( getName() )) return false;
    return true;
  }
  
  public int hashCode(){
    return myName.hashCode();
  }
}
