/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.p2p;

import java.io.Serializable;

public abstract class P2PEvent implements Serializable{

  public abstract void handle(iP2PEventHandler aHandler);
}
