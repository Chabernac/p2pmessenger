/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.android.drinklist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;

public class DrinkFactory {
  private static Map<String, Drink> myDrinks = new HashMap<String, Drink>();
  
  private final Activity myActivity;
  
  public DrinkFactory( Activity aActivity ) {
    super();
    myActivity = aActivity;
  }
  
  public Drink getDrink(String aFullDrinkName) throws DrinkException{
    String theDrinkType = aFullDrinkName.substring( 0, aFullDrinkName.indexOf( "_" ) );
    String theDrink = aFullDrinkName.substring( aFullDrinkName.indexOf( "_" ) + 1, aFullDrinkName.length() );
    return getDrink( theDrinkType, theDrink );
  }

  public Drink getDrink(String aDrinkType, String aName) throws DrinkException{
    String theDrink = aDrinkType + "_" + aName;
    if(!myDrinks.containsKey( theDrink )){
      try{
        Field theDrinkField = R.id.class.getField( theDrink );
        int theImageResource = theDrinkField.getInt( R.id.class ); 
        Drink theNewDrink = new Drink( aDrinkType, aName, theImageResource );
        loadOptions( theNewDrink );
        myDrinks.put(theDrink, theNewDrink);
      }catch(Exception e){
        throw new DrinkException("Drink with '"  + theDrink + "' could not be loaded");
      }
    }
    return myDrinks.get(theDrink);
  }

  private void loadOptions(Drink aDrink){
    BufferedReader theReader =  null;
    try {
      theReader = new BufferedReader( new InputStreamReader( myActivity.getAssets().open( aDrink.getName() + ".txt" ) ));
      String theLine = null;
      while((theLine = theReader.readLine()) != null){
        aDrink.addDrinkOption( theLine );
      }
    } catch ( IOException e ) {
    } finally {
      try {
        if(theReader != null){
          theReader.close();
        }
      } catch ( IOException e ) {
      }
    }
  }
}
