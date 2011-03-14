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
  public long getPrice( Drink aDrink ) {
    if(myPrices.containsKey(  aDrink.getName() )) return Long.parseLong(myPrices.getProperty( aDrink.getName())) * 100 ;
    if(myPrices.containsKey( aDrink.getDrinkType() )) return Long.parseLong(myPrices.getProperty( aDrink.getDrinkType())) * 100;
    return 150;
  }

}
