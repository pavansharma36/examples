package org.one.utils;

import org.one.utils.id.Base32Utils;
import org.one.utils.id.IdGenerator;

public class App {

  public static void main(String[] args) {
    System.out.println(Base32Utils.toString(Integer.MAX_VALUE));
    System.out.println(Base32Utils.toString(0));

    System.out.println(new IdGenerator.MachineIdToken().value(Object.class));
    System.out.println(new IdGenerator.ProcessIdToken().value(Object.class));

    for (int i = 0; i < 100; i++) {
      System.out.println(new IdGenerator.CounterToken(5).value(Object.class));
    }
  }

}
