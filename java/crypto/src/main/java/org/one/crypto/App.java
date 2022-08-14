package org.one.crypto;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class App {

  public static void main(String[] args)
      throws Exception {
    InputStream in = new FileInputStream("/home/pavan/Desktop/keystore.p12");
    CryptKeys keys = new CryptKeys(in, "password", "PKCS12");
    byte[] iv = "7cVgr5cbdCZVw5WY".getBytes(StandardCharsets.UTF_8);
    CryptManager manager = new CryptManager(iv, keys);

    //SafeTuple st = manager.encrypt(KeyType.DEFAULT, "pavan".getBytes(StandardCharsets.UTF_8));

    SafeTuple nst = SafeTuple.parse("default.202208141827,KJhROfHfBp053EYCyJlJaQ==");
    System.out.println(new String(manager.decrypt(nst.getKey(), nst.getEncryptedValue())));
  }

}
