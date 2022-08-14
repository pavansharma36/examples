package org.one.crypto;

import java.util.Base64;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SafeTuple {

  public static final String SEPARATOR = ",";

  public static SafeTuple parse(String s) {
    String[] payload = s.split(SEPARATOR);
    if (payload.length != 2) {
      throw new CryptException("Invalid argument");
    }
    return new SafeTuple(payload[0], Base64.getDecoder().decode(payload[1]));
  }

  @NonNull
  private final String key;
  @NonNull
  private final byte[] encryptedValue;

  public String encoded() {
    return key + SEPARATOR + Base64.getEncoder().encodeToString(encryptedValue);
  }

  public String value() {
    return Base64.getEncoder().encodeToString(encryptedValue);
  }

}
