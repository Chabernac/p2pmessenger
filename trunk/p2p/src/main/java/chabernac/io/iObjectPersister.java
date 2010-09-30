/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface iObjectPersister<T extends Object> {
  public void persistObject(T anObject, OutputStream anOutputStream) throws IOException;
  public T loadObject(InputStream anInputStream) throws IOException;
}
