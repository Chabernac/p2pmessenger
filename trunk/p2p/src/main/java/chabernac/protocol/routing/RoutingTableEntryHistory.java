/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.routing;

import java.io.PrintWriter;
import java.io.StringWriter;

public class RoutingTableEntryHistory {
  private final RoutingTableEntry myRoutingTableEntry;
  private final Exception myStackTrace;

  public RoutingTableEntryHistory ( RoutingTableEntry anRoutingTableEntry ) {
    super();
    myRoutingTableEntry = anRoutingTableEntry;
    myStackTrace = new Exception();
    myStackTrace.fillInStackTrace();
  }

  public RoutingTableEntry getRoutingTableEntry() {
    return myRoutingTableEntry;
  }

  public Exception getStackTrace() {
    return myStackTrace;
  }
  
  public String toString(){
    StringWriter theStringWriter= new StringWriter();
    PrintWriter theWriter = new PrintWriter(theStringWriter);
    myStackTrace.printStackTrace( theWriter );
    return myRoutingTableEntry.toString() + "\r\n" + theStringWriter.getBuffer();
  }
}
