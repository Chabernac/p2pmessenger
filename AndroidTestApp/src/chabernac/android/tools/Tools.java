/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.android.tools;

import java.lang.reflect.Field;

import android.app.Activity;
import chabernac.android.drinklist.R;

public class Tools {
  public static String translate(Activity anActivity, String aString){
    try {
      Field theField = R.string.class.getField( aString );
      return anActivity.getResources().getString( theField.getInt( R.string.class ) );
    } catch ( Exception e ) {
      return aString;
    }
  }
}
