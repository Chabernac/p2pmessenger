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
  public static enum Action{ADD, DELETE};
  private final Action myAction; 

  public RoutingTableEntryHistory ( RoutingTableEntry anRoutingTableEntry, Action anAction ) {
    super();
    myRoutingTableEntry = anRoutingTableEntry;
    myAction = anAction;
    myStackTrace = new Exception();
    myStackTrace.fillInStackTrace();
  }

  public RoutingTableEntry getRoutingTableEntry() {
    return myRoutingTableEntry;
  }

  public String getStackTrace() {
    StringWriter theStringWriter= new StringWriter();
    PrintWriter theWriter = new PrintWriter(theStringWriter);
    myStackTrace.printStackTrace( theWriter );
    return theStringWriter.getBuffer().toString();
  }
  
  public Action getAction() {
    return myAction;
  }

  public String toString(){
    return myRoutingTableEntry.toString() + "\r\n" + getStackTrace();
  }
}
