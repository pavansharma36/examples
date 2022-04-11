package org.one.utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.MurmurHash3;

public class IdPrefixGenerator {

  private final Function<Class<?>, String> function;

  public IdPrefixGenerator() {
    this(4);
  }

  public IdPrefixGenerator(int length) {
    assert length > 0;
    assert length <= 8;
    if(length > 4) {
      function = clazz -> {
        long[] hash = MurmurHash3.hash128(clazz.getName().getBytes(StandardCharsets.UTF_8));
        ByteBuffer buffer = ByteBuffer.allocate(hash.length * Long.BYTES);
        for(long l : hash) {
          buffer.putLong(l);
        }
        String s = Base64.encodeBase64URLSafeString(buffer.array());

        return s.substring(0, length);
      };
    } else {
      function = clazz -> {
        int hash = MurmurHash3.hash32x86(clazz.getName().getBytes(StandardCharsets.UTF_8));
        byte[] bytes = new byte[] {
            (byte)(hash >>> 16),
            (byte)(hash >>> 8),
            (byte) hash
        };
        String s = Base64.encodeBase64String(bytes);
        return s.substring(s.length() - length);
      };
    }
  }

  public String generate(Class<?> clazz) {
    return function.apply(clazz);
  }

}
