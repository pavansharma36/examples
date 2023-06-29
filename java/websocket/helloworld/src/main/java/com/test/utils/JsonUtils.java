package com.test.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

  private static final ObjectMapper MAPPER;

  static {
    MAPPER = new ObjectMapper();
    MAPPER.findAndRegisterModules();
    MAPPER.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
    MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
  }

  public static String toJson(Object o) {
    try {
      return MAPPER.writeValueAsString(o);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> T fromJson(String json, Class<T> clazz) {
    try {
      return MAPPER.readValue(json, clazz);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

}
