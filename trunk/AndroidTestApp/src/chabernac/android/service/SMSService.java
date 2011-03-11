/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * since sending an sms is not exposed as an intend we try to do it ourselfs
 * so that the user can choose how he wants to send his drink order list
 * by mail or by sms 
 */
public class SMSService extends Service {

  @Override
  public IBinder onBind( Intent aIntent ) {
    // TODO Auto-generated method stub
    return null;
  }

}
