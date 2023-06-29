package com.test.codec;

import com.test.dto.MessageDTO;
import com.test.utils.JsonUtils;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageDecoder implements Decoder.Text<MessageDTO> {

  @Override
  public MessageDTO decode(String s) throws DecodeException {
    try {
      return JsonUtils.fromJson(s, MessageDTO.class);
    } catch (RuntimeException e) {
      log.error("Error while decoding {}", e.getMessage(), e);
      throw new DecodeException(s, e.getMessage());
    }
  }

  @Override
  public boolean willDecode(String s) {
    return (s != null);
  }

  @Override
  public void init(EndpointConfig endpointConfig) {
    // Custom initialization logic
  }

  @Override
  public void destroy() {
    // Close resources
  }
}
