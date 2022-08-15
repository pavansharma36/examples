package org.one.crypto;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public class App {

  public static void main(String[] args) {
    InputStream in = App.class.getClassLoader().getResourceAsStream("keystore.p12");
    CryptKeys keys = new CryptKeys(in, "password", "PKCS12");
    byte[] iv = "7cVgr5cbdCZVw5WY".getBytes(StandardCharsets.UTF_8);
    CryptManager manager = new CryptManager(iv, keys);

    List<SafeTuple> tuple = new LinkedList<>();
    for (int i= 0 ; i< 5 ; i++) {
      tuple.add(manager.encrypt(KeyType.DEFAULT, "pavan".getBytes(StandardCharsets.UTF_8)));
    }

    for (SafeTuple t:tuple) {
      System.out.print("Encrypted value : " + t.value() + " , ");
      System.out.println("Decrypted value : " + new String(manager.decrypt(t.getKey(), t.getEncryptedValue())));
    }
  }

}
