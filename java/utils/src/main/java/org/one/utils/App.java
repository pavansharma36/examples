package org.one.utils;

import org.one.utils.id.Base32Utils;

public class App {

  public static void main(String[] args) {
    System.out.println(Base32Utils.toString(Integer.MAX_VALUE));
    System.out.println(Base32Utils.toString(0));
  }

}
