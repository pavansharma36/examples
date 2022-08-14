package org.one.crypto;

import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CryptKeys {
  private final Map<KeyType, KeyInfo> latestKeys;
  private final Map<String, KeyInfo> keys;

  public CryptKeys(InputStream keystoreInputStream, String keystorePassword, String storeType) {
    try {
      KeyStore ks = KeyStore.getInstance(storeType);
      ks.load(keystoreInputStream, keystorePassword.toCharArray());

      List<KeyType> keyTypes = Arrays.asList(KeyType.values());

      Enumeration<String> alias = ks.aliases();

      Map<KeyType, String> latestKeyMap = new EnumMap<>(KeyType.class);
      this.latestKeys = new EnumMap<>(KeyType.class);
      this.keys = new HashMap<>();
      while (alias.hasMoreElements()) {
        String a = alias.nextElement();
        Optional<KeyType>
            matchingKeyType = keyTypes.stream().filter(kt -> a.startsWith(kt.getPrefix())).findAny();
        if (matchingKeyType.isPresent()) {
          KeyType kt = matchingKeyType.get();

          Key key = ks.getKey(a, keystorePassword.toCharArray());
          KeyInfo keyInfo = new KeyInfo(kt, a, key);
          if (latestKeyMap.containsKey(kt)) {
            String aa = latestKeyMap.get(kt);
            if (a.compareTo(aa) > 0) {
              latestKeys.put(kt, keyInfo);
              latestKeyMap.put(kt, a);
            }
          } else {
            latestKeys.put(kt, keyInfo);
            latestKeyMap.put(kt, a);
          }

          keys.put(a, keyInfo);
        } else {
          log.warn("Key `{}` doesn't match with any prefix", a);
        }
      }
    } catch (Exception e) {
      throw new CryptException(e.getMessage(), e);
    }
  }

  public KeyInfo getLatestKey(KeyType keyType) {
    KeyInfo key = latestKeys.get(keyType);
    if (key == null) {
      throw new CryptException("Key type not found " + keyType);
    }
    return key;
  }

  public KeyInfo getKey(String alias) {
    KeyInfo key = keys.get(alias);
    if (key == null) {
      throw new CryptException("Key type not found " + alias);
    }
    return key;
  }

  public boolean isLatest(String alias) {
    return alias.equals(getLatestKey(getKey(alias).getType()).getAlias());
  }
}
