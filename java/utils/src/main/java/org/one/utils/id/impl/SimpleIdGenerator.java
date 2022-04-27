package org.one.utils.id.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.commons.codec.binary.Base32;
import org.one.utils.id.IdGenerator;
import org.one.utils.id.IdPrefixGenerator;
import org.one.utils.random.XorShiftRandom;

public class SimpleIdGenerator implements IdGenerator {

  private static final XorShiftRandom RANDOM = new XorShiftRandom(XorShiftRandom.randomSeed());
  private static final Map<Class<?>, String> PREFIX_MAP = new ConcurrentHashMap<>();

  private final Supplier<String> function;
  private final Function<Class<?>, String> prefixSupplier;

  public SimpleIdGenerator(int length, int prefixLength) {
    function = () -> {
      byte[] bytes = new byte[length];
      RANDOM.nextBytes(bytes);
      String suffix = new Base32().encodeAsString(bytes);
      return suffix.substring(0, length);
    };
    for(int i = 0 ; i <= Byte.MAX_VALUE ; i++) {
      // warm up. required for unique.
      function.get();
    }
    IdPrefixGenerator prefixGenerator = new IdPrefixGenerator(prefixLength);
    this.prefixSupplier = prefixGenerator::generate;
  }

  public String nextId(Class<?> forClazz) {
    return String.format("%S%S", PREFIX_MAP.computeIfAbsent(forClazz, dummy -> prefixSupplier.apply(forClazz)),
        function.get());
  }

  @Override
  public List<TokenValue> parse(String id) {
    if(id == null) {
      return null;
    }
    return null;
  }
}
