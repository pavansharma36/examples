package com.test.codec;

import com.test.dto.MessageDTO;
import com.test.utils.JsonUtils;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class MessageDecoder implements Decoder.Text<MessageDTO> {

  @Override
  public MessageDTO decode(String s) throws DecodeException {
    return JsonUtils.fromJson(s, MessageDTO.class);
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
