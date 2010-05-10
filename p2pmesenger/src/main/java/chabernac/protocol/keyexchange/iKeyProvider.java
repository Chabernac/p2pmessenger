package chabernac.protocol.keyexchange;

import java.security.PublicKey;

import javax.crypto.SecretKey;

public interface iKeyProvider {
  public PublicKey getPublicKeyForUser(String aUser) throws KeyException;
  public SecretKey getSecretKeyForUser(String aUser) throws KeyException;
}
