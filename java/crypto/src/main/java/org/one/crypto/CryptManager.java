package org.one.crypto;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CryptManager {

  private static final int IV_LENGTH = 16;
  private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
  private static final Random RANDOM = new SecureRandom();

  private final CryptKeys keys;

  public SafeTuple encrypt(KeyType keyType, byte[] data) {
    KeyInfo keyInfo = keys.getLatestKey(keyType);
    try {
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      byte[] iv = randomIV();
      cipher.init(Cipher.ENCRYPT_MODE, keyInfo.getKey(), new IvParameterSpec(iv));
      byte[] cipherText = cipher.doFinal(data);
      ByteBuffer buffer = ByteBuffer.allocate(iv.length + cipherText.length);
      buffer.put(iv);
      buffer.put(cipherText);
      return new SafeTuple(keyInfo.getAlias(), buffer.array());
    } catch (Exception e) {
      throw new CryptException(e.getMessage(), e);
    }
  }

  private byte[] randomIV() {
    byte[] iv = new byte[IV_LENGTH];
    RANDOM.nextBytes(iv);
    return iv;
  }

  public byte[] decrypt(String keyAlias, byte[] encryptedData) {
    KeyInfo keyInfo = keys.getKey(keyAlias);
    try {
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      byte[] iv = new byte[IV_LENGTH];
      System.arraycopy(encryptedData, 0, iv, 0, IV_LENGTH);

      int cipherLength = encryptedData.length - IV_LENGTH;
      byte[] cipherText = new byte[cipherLength];
      System.arraycopy(encryptedData, IV_LENGTH, cipherText, 0, cipherLength);
      cipher.init(Cipher.DECRYPT_MODE, keyInfo.getKey(), new IvParameterSpec(iv));
      return cipher.doFinal(cipherText);
    } catch (Exception e) {
      throw new CryptException(e.getMessage(), e);
    }
  }

}
