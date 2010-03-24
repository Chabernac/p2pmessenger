/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.protocol.keyexchange;

import chabernac.encryption.iPublicPrivateKeyEncryption;

public interface iPublicPrivateKeyEncryptionFactory {
  public iPublicPrivateKeyEncryption createEncryption() throws PublicPrivateKeyEnctryptionException;
}
