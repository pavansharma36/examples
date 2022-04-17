package org.one.utils;

import java.util.function.Supplier;
import org.apache.commons.codec.binary.Base32;
import org.one.utils.random.XorShiftRandom;

public class IdGenerator {

  private static final XorShiftRandom RANDOM = new XorShiftRandom(XorShiftRandom.randomSeed());

  private final Supplier<String> function;

  public IdGenerator(int length) {
    this(length, new IdPrefixGenerator().generate(Object.class));
  }

  public IdGenerator(String schemaId) {
    this(12, schemaId);
  }

  public IdGenerator(int length, String schemaId) {
    function = () -> {
      byte[] bytes = new byte[length * Byte.SIZE];
      RANDOM.nextBytes(bytes);
      return String.format("%S%S", schemaId, new Base32().encodeAsString(bytes).substring(0, length));
    };
  }

  public String nextId() {
    return function.get();
  }

}
