package org.one.utils;


import org.junit.Assert;
import org.junit.Test;

public class IdPrefixGeneratorTest {

  @Test
  public void test_generate() {
    IdPrefixGenerator generator = new IdPrefixGenerator();
    String objectPrefix = generator.generate(Object.class);
    System.out.println("Prefix for object " + objectPrefix);
    Assert.assertNotNull(objectPrefix);
  }

  @Test
  public void test_generate_length() {
    for(int i = 1; i <=8 ; i++) {
      IdPrefixGenerator generator = new IdPrefixGenerator(i);
      String objectPrefix = generator.generate(Object.class);
      System.out.printf("Prefix for length %s object %s%n", i, objectPrefix);
      Assert.assertEquals(objectPrefix.length(), i);
    }
  }
}
