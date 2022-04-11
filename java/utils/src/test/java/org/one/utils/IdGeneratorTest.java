package org.one.utils;

import org.junit.Test;

public class IdGeneratorTest {

  @Test
  public void nextId_test() {
    IdGenerator generator = new IdGenerator(new IdPrefixGenerator().generate(Object.class));

    String randomId = generator.nextId();
    System.out.println("Next id for object is "+ randomId);
  }
}
