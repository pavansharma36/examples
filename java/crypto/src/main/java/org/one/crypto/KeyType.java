package org.one.crypto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KeyType {
  DEFAULT("default"),
  DB("db");

  private final String prefix;
}
