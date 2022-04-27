package org.one.utils.id;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.binary.Base32;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Base32Utils {

  private static final Base32 BASE_32 = new Base32();

  public static String toString(byte[] value) {
    return BASE_32.encodeAsString(value);
  }

  public static String toString(short value) {
    byte[] bytes = new byte[] {
        (byte) (value >>> 7),
        (byte) (value << 1)
    };
    return toString(bytes).substring(0, 3);
  }

  public static String toString(int value) {
    byte[] bytes = new byte[] {
        0,
        (byte) (value >>> 24),
        (byte) (value >>> 16),
        (byte) (value >>> 8),
        (byte) (value)
    };
    return toString(bytes);
  }

  public static String toString(long value) {
    byte[] bytes = new byte[] {
        (byte) (value >>> 32),
        (byte) (value >>> 24),
        (byte) (value >>> 16),
        (byte) (value >>> 8),
        (byte) (value)
    };
    return toString(bytes);
  }

}
