/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.android.drinklist;

import java.util.Properties;

public class PriceProvider implements iPriceProvider {
  
  private final Properties myPrices;
  
  public PriceProvider(Properties aProperties){
    myPrices = aProperties;
  }

  @Override
  public float getPrice( Drink aDrink ) {
    if(myPrices.containsKey(  aDrink.getName() )) return Float.parseFloat( myPrices.getProperty(aDrink.getName()) );
    if(myPrices.containsKey( aDrink.getDrinkType() )) return Float.parseFloat( myPrices.getProperty(aDrink.getDrinkType()) );
    return 1.50f;
  }
}
