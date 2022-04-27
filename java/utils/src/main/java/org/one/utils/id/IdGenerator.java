package org.one.utils.id;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.digest.MurmurHash3;
import org.one.utils.random.XorShiftRandom;

public interface IdGenerator {

  String nextId(Class<?> forClazz);

  List<TokenValue> parse(String id);

  @Getter
  @RequiredArgsConstructor
  abstract class Token<T extends Serializable> {
    protected final String name;
    protected final int length;
    protected final Function<Class<?>, T> function;
    public abstract String value(Class<?> forClazz);
  }

  class ShortToken extends Token<Short> {
    public ShortToken(String name, Function<Class<?>, Short> function) {
      super(name, 3, function);
    }
    @Override
    public String value(Class<?> forClazz) {
      return Base32Utils.toString(function.apply(forClazz));
    }
  }

  class TimestampToken extends Token<Long> {

    private static final String TIMESTAMP = "timestamp";

    public TimestampToken() {
      super(TIMESTAMP, 7, clazz -> System.currentTimeMillis());
    }

    public TimestampToken(int length,
                          Function<Class<?>, Long> function) {
      super(TIMESTAMP, length, function);
    }

    @Override
    public String value(Class<?> forClazz) {
      String s = Base32Utils.toString(function.apply(forClazz) / 1000);
      return s.substring(s.length() - length);
    }
  }

  class SchemaIdToken extends Token<String> {

    private static final String SCHEMA_ID = "schema_id";
    private static final Map<Class<?>, String> PREFIX_MAP = new ConcurrentHashMap<>();

    public SchemaIdToken(int length) {
      super(SCHEMA_ID, length, length > 4 ?
          clazz -> {
            long[] hash = MurmurHash3.hash128(clazz.getName().getBytes(StandardCharsets.UTF_8));
            ByteBuffer buffer = ByteBuffer.allocate(hash.length * Long.BYTES);
            for(long l : hash) {
              buffer.putLong(l);
            }
            String s = new Base32().encodeAsString(buffer.array());

            return s.substring(0, length);
          } : clazz -> {
        int hash = MurmurHash3.hash32x86(clazz.getName().getBytes(StandardCharsets.UTF_8));
        byte[] bytes = new byte[] {
            (byte) (hash >>> 16),
            (byte) (hash >>> 8),
            (byte) hash
        };
        String s = new Base32().encodeAsString(bytes);
        return s.substring(0, length);
      });
    }

    @Override
    public String value(Class<?> forClazz) {
      return PREFIX_MAP.computeIfAbsent(forClazz, dummy -> function.apply(forClazz));
    }
  }

  class RandomToken extends Token<String> {

    private static final String RANDOM = "random";
    private static final XorShiftRandom XOR_SHIFT_RANDOM = new XorShiftRandom(XorShiftRandom.randomSeed());

    public RandomToken(int length) {
      super(RANDOM, length, clazz -> {
        byte[] bytes = new byte[length];
        XOR_SHIFT_RANDOM.nextBytes(bytes);
        String suffix = new Base32().encodeAsString(bytes);
        return suffix.substring(0, length);
      });
    }

    @Override
    public String value(Class<?> forClazz) {
      return function.apply(forClazz);
    }
  }

  @Getter
  @Setter
  class TokenValue {
    private String name;
    private Serializable value;
  }

}
