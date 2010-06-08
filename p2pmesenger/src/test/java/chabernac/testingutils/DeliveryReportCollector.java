/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.testingutils;

import java.util.ArrayList;
import java.util.List;

import chabernac.protocol.message.DeliveryReport;
import chabernac.protocol.message.iDeliverReportListener;

public class DeliveryReportCollector implements iDeliverReportListener {
  private List< DeliveryReport > myDeliveryReports = new ArrayList< DeliveryReport >();

  @Override
  public void acceptDeliveryReport( DeliveryReport aDeliverReport ) {
    myDeliveryReports.add(aDeliverReport);
  }
  
  public List<DeliveryReport> getDeliveryReports(){
    return myDeliveryReports;
  }

}
