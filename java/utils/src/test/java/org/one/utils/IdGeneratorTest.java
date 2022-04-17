package org.one.utils;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class IdGeneratorTest {

  @Test
  public void nextId_test() {
    int prefixLength = 5;
    for(int i = 8; i <= 32; i++) {
      IdGenerator generator = new IdGenerator(i, prefixLength);

      String randomId = generator.nextId(Object.class);
      System.out.println("Next id for object is " + randomId);
      Assert.assertEquals(i + prefixLength, randomId.length());
    }
  }
}
