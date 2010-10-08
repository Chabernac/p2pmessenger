/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.message;

import java.io.Serializable;

public enum MessageIndicator implements Serializable{
  ENCRYPTED, TO_BE_ENCRYPTED, CLOSED_ENVELOPPE;
}
