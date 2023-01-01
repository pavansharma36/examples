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

  private static final int OBFUSCATE_LENGTH = 5;

  private static final int IV_LENGTH = 16;
  private static final char[] OBFUSCATE_CHARS;
  private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
  private static final Random RANDOM = new SecureRandom();

  static {
    List<Character> chars = new LinkedList<>();
    for (int i = 'A' ; i <= 'Z' ; i++) {
      chars.add((char) i);
    }
    for (int i = 'a' ; i <= 'z' ; i++) {
      chars.add((char) i);
    }
    for (int i = '0' ; i <= '9' ; i++) {
      chars.add((char) i);
    }

    OBFUSCATE_CHARS = new char[chars.size()];
    int i = 0;
    for(Character c : chars) {
      OBFUSCATE_CHARS[i++] = c;
    }
  }
  private final CryptKeys keys;

  public SafeTuple encrypt(KeyType keyType, byte[] data) {
    KeyInfo keyInfo = keys.getLatestKey(keyType);
    try {
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      byte[] iv = randomIV();
      cipher.init(Cipher.ENCRYPT_MODE, keyInfo.getKey(), new IvParameterSpec(iv));
      byte[] cipherText = cipher.doFinal(appendObfuscate(data));
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

  private byte[] appendObfuscate(byte[] data) {
    byte[] newData = new byte[data.length + OBFUSCATE_LENGTH];
    for(int i = 0; i < OBFUSCATE_LENGTH; i++) {
      newData[i] = (byte) OBFUSCATE_CHARS[RANDOM.nextInt(OBFUSCATE_CHARS.length)];
    }
    System.arraycopy(data, 0, newData, 5, data.length);
    return newData;
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
      return removeObfuscate(cipher.doFinal(cipherText));
    } catch (Exception e) {
      throw new CryptException(e.getMessage(), e);
    }
  }

  private byte[] removeObfuscate(byte[] data) {
    byte[] newData = new byte[data.length - OBFUSCATE_LENGTH];
    System.arraycopy(data, 5, newData, 0, data.length - OBFUSCATE_LENGTH);
    return newData;
  }

}
