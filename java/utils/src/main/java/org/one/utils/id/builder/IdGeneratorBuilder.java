package org.one.utils.id.builder;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.one.utils.id.IdGenerator;
import org.one.utils.id.impl.AdvancedIdGenerator;
import org.one.utils.id.impl.SimpleIdGenerator;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IdGeneratorBuilder {

  public static SimpleIdGeneratorBuilder simple() {
    return new SimpleIdGeneratorBuilder();
  }

  public static AdvancedIdGeneratorBuilder advanced() {
    return new AdvancedIdGeneratorBuilder();
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class SimpleIdGeneratorBuilder {
    public SimpleIdGenerator build(int prefixLength, int randomLength) {
      return new SimpleIdGenerator(randomLength, prefixLength);
    }
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class AdvancedIdGeneratorBuilder {
    private final List<IdGenerator.Token<?>> tokens = new LinkedList<>();

    public AdvancedIdGeneratorBuilder appendShortToken(String name, Supplier<Short> supplier) {
      tokens.add(new IdGenerator.ShortToken(name, forClazz -> supplier.get()));
      return this;
    }

    public AdvancedIdGeneratorBuilder appendSchemaIdToken(int length) {
      tokens.add(new IdGenerator.SchemaIdToken(length));
      return this;
    }

    public AdvancedIdGeneratorBuilder appendTimestamp() {
      tokens.add(new IdGenerator.TimestampToken());
      return this;
    }

    public AdvancedIdGeneratorBuilder appendRandom(int length) {
      tokens.add(new IdGenerator.RandomToken(length));
      return this;
    }

    public AdvancedIdGenerator build() {
      tokens.stream().collect(Collectors.groupingBy(IdGenerator.Token::getName)).entrySet()
          .stream().filter(c -> c.getValue().size() > 1).findAny().ifPresent(t -> {
            throw new RuntimeException("Duplicate token : " + t.getKey());
          });
      return new AdvancedIdGenerator(tokens);
    }
  }

}
