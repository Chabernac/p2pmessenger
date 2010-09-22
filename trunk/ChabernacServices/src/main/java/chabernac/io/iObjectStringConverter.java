/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.io.IOException;

public interface iObjectStringConverter <T extends Object>{
  public String toString(T anObject) throws IOException;
  public T getObject(String aString) throws IOException;
}
