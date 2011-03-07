/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.android.testapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MailReceiver extends BroadcastReceiver {

  @Override
  public void onReceive( Context aContext, Intent aIntent ) {
      System.out.println("receive: " + aIntent.toString());
      System.out.println(aIntent.getExtras().get("EXTRA_TEXT"));
  }

}
