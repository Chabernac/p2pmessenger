/*
 * Copyright (c) 2003 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.chart;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 *
 * @version v1.0.0      Oct 3, 2005
 *<pre><u><i>Version History</u></i>
 *
 * v1.0.0 Oct 3, 2005 - initial release       - Guy Chauliac
 *
 *</pre>
 *
 * @author <a href="mailto:guy.chauliac@axa.be"> Guy Chauliac </a>
 */
public class TimeFormatter implements iValueFormatter {
  private SimpleDateFormat myFormat = null;
  
  public TimeFormatter(String aFormat){
    myFormat = new SimpleDateFormat(aFormat);
  }

  public String formatValue(double aDouble) {
    Date theDate = new Date();
    theDate.setTime((long)aDouble);
    return myFormat.format(theDate);
  }

}
