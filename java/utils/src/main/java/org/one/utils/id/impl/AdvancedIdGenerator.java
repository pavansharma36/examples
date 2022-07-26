package org.one.utils.id.impl;

import java.util.List;
import org.one.utils.id.IdGenerator;

public class AdvancedIdGenerator implements IdGenerator {

  private final int length;
  private final List<Token<?>> tokens;

  public AdvancedIdGenerator(List<Token<?>> tokens) {
    this.tokens = tokens;
    this.length = tokens.stream().mapToInt(Token::getLength).sum();
  }

  @Override
  public String nextId(Class<?> forClazz) {
    StringBuilder sb = new StringBuilder(length);
    tokens.forEach(t -> sb.append(t.value(forClazz)));
    return sb.toString();
  }

  @Override
  public List<TokenValue> parse(String id) {
    return null;
  }
}
