package org.one.utils;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.apache.commons.codec.binary.Base32;
import org.junit.Assert;
import org.junit.Test;
import org.one.utils.id.Base32Utils;
import org.one.utils.id.IdGenerator;
import org.one.utils.id.builder.IdGeneratorBuilder;
import org.one.utils.id.impl.SimpleIdGenerator;

public class IdGeneratorTest {

  @Test
  public void nextId_simple_test() {
    int prefixLength = 5;
    for(int i = 8; i <= 32; i++) {
      IdGenerator generator = IdGeneratorBuilder.simple().build(prefixLength, i);

      String randomId = generator.nextId(Object.class);
      System.out.println("Next id for object is " + randomId);
      Assert.assertEquals(i + prefixLength, randomId.length());
    }
  }

  @Test
  public void nextId_advanced_test() {

    IdGenerator generator = IdGeneratorBuilder.advanced()
        .appendRandom(6)
        .appendTimestamp()
        .appendShortToken("sh", () -> (short) new Random().nextInt())
        .appendSchemaIdToken(4)
        .build();
    int count = 1000;

    long time = System.currentTimeMillis();
    for(int i = 0; i < count ; i++) {
      String randomId = generator.nextId(List.class);
      System.out.println(randomId);
      Assert.assertEquals(20, randomId.length());
    }
    Duration d = Duration.ofMillis(System.currentTimeMillis() - time);
    System.out.printf("Generated %s randoms in %s seconds", count, d.getSeconds());

  }



}
