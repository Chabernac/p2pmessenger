/*
 * Copyright (c) 2003 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */

package chabernac.chart;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *
 *
 * @version v1.0.0      Sep 30, 2005
 *<pre><u><i>Version History</u></i>
 *
 * v1.0.0 Sep 30, 2005 - initial release       - Guy Chauliac
 *
 *</pre>
 *
 * @author <a href="mailto:guy.chauliac@axa.be"> Guy Chauliac </a>
 */
public class DateFormatter implements iValueFormatter {
  public static final String DAY_FORMAT = "dd/MM/yyyy";
  public static final String MONTH_FORMAT = "MMM yyyy";
  public static final String YEAR_FORMAT = "yyyy";
  
  public static final int DAY_UNIT = 1;
  public static final int MONTH_UNIT = 2;
  public static final int YEAR_UNIT = 3;
  
  private String myDayFormat = DAY_FORMAT;
  private int myUnit = DAY_UNIT;
  
  public DateFormatter(int aUnit){
    this(aUnit, getDefaultFormatForUnit(aUnit));
  }
  
  public DateFormatter(int aUnit, String aDayFormat){
    myUnit = aUnit;
    myDayFormat = aDayFormat;
  }

  public String formatValue(double aDouble) {
    GregorianCalendar theDate = new GregorianCalendar();
    switch(myUnit){
        case DAY_UNIT: theDate.add(Calendar.DAY_OF_YEAR, (int)aDouble); break;
        case MONTH_UNIT: theDate.add(Calendar.MONTH, (int)aDouble); break; 
        case YEAR_UNIT: theDate.add(Calendar.YEAR, (int)aDouble); break;
    }
    if(Math.abs(aDouble - Math.floor(aDouble)) == 0){
      SimpleDateFormat theFormat = new SimpleDateFormat(myDayFormat);
      return theFormat.format(theDate.getTime());
    } else {
      return "";
    }
  }
  
  public static String getDefaultFormatForUnit(int aUnit){
    switch(aUnit){
      case DAY_UNIT: return DAY_FORMAT;
      case MONTH_UNIT: return MONTH_FORMAT;
      case YEAR_UNIT: return YEAR_FORMAT;
      default: return DAY_FORMAT;
    }
  }

}
