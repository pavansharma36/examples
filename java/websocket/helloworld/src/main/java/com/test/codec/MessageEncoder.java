package com.test.codec;

import com.test.dto.MessageDTO;
import com.test.utils.JsonUtils;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class MessageEncoder implements Encoder.Text<MessageDTO> {


  @Override
  public String encode(MessageDTO message) throws EncodeException {
    return JsonUtils.toJson(message);
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