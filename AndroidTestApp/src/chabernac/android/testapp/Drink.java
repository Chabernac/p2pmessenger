/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.android.testapp;

public class Drink {
  private final String myName;
  private final int myImageResource;

  public Drink( String aName, int aImageResource ) {
    super();
    myName = aName;
    myImageResource = aImageResource;
  }
  public String getName() {
    return myName;
  }
  public int getImageResource() {
    return myImageResource;
  }
}
