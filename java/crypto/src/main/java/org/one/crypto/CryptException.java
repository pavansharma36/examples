package org.one.crypto;

/**
 * Exception to throw on any cryptography errors.
 */
public class CryptException extends RuntimeException {
  public CryptException(String message) {
    super(message);
  }
  public CryptException(String message, Throwable cause) {
    super(message, cause);
  }
  public CryptException(Throwable cause) {
    super(cause);
  }
}
