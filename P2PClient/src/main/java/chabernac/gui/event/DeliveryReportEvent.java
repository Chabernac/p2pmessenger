/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.gui.event;

import chabernac.events.Event;
import chabernac.protocol.message.DeliveryReport;

public class DeliveryReportEvent extends Event {
  private static final long serialVersionUID = 3509249237628968778L;
  
  private final DeliveryReport myDeliveryReport;

  public DeliveryReportEvent ( DeliveryReport anDeliverReport ) {
    myDeliveryReport = anDeliverReport;
  }

  public DeliveryReport getDeliveryReport() {
    return myDeliveryReport;
  }
}
