package org.one.crypto;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CryptManager {

  private static final int obfuscateLength = 5;
  private static final char[] obfuscateChars;
  private static final String algorithm = "AES/CBC/PKCS5Padding";

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

    obfuscateChars = new char[chars.size()];
    int i = 0;
    for(Character c : chars) {
      obfuscateChars[i++] = c;
    }
  }

  private final byte[] ivBytes;
  private final CryptKeys keys;

  public SafeTuple encrypt(KeyType keyType, byte[] data) {
    KeyInfo keyInfo = keys.getLatestKey(keyType);
    try {
      Cipher cipher = Cipher.getInstance(algorithm);
      cipher.init(Cipher.ENCRYPT_MODE, keyInfo.getKey(), new IvParameterSpec(ivBytes));
      byte[] cipherText = cipher.doFinal(data);
      return new SafeTuple(keyInfo.getAlias(), cipherText);
    } catch (Exception e) {
      throw new CryptException(e.getMessage(), e);
    }
  }

  public byte[] decrypt(String keyAlias, byte[] encryptedData) {
    KeyInfo keyInfo = keys.getKey(keyAlias);
    try {
      Cipher cipher = Cipher.getInstance(algorithm);
      cipher.init(Cipher.DECRYPT_MODE, keyInfo.getKey(), new IvParameterSpec(ivBytes));
      return cipher.doFinal(encryptedData);
    } catch (Exception e) {
      throw new CryptException(e.getMessage(), e);
    }
  }

}
