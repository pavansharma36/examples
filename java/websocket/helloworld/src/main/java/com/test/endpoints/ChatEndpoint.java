package com.test.endpoints;

import com.test.codec.MessageDecoder;
import com.test.codec.MessageEncoder;
import com.test.dto.MessageDTO;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/chat/{username}",
    decoders = MessageDecoder.class,
    encoders = MessageEncoder.class)
public class ChatEndpoint {

  private Session session;
  private static final Set<ChatEndpoint> chatEndpoints
      = new CopyOnWriteArraySet<>();
  private static final HashMap<String, String> users = new HashMap<>();

  @OnOpen
  public void onOpen(
      Session session,
      @PathParam("username") String username) {

    this.session = session;
    chatEndpoints.add(this);
    users.put(session.getId(), username);

    MessageDTO message = new MessageDTO();
    message.setFrom(username);
    message.setContent("Connected!");
    broadcast(message);
  }

  @OnMessage
  public void onMessage(Session session, MessageDTO message) {
    message.setFrom(users.get(session.getId()));
    broadcast(message);
  }

  @OnClose
  public void onClose(Session session) {
    chatEndpoints.remove(this);
    MessageDTO message = new MessageDTO();
    message.setFrom(users.get(session.getId()));
    message.setContent("Disconnected!");
    broadcast(message);
  }

  @OnError
  public void onError(Session session, Throwable throwable) {
    // Do error handling here
  }

  private static void broadcast(MessageDTO message) {

    chatEndpoints.forEach(endpoint -> {
      synchronized (endpoint) {
        try {
          endpoint.session.getBasicRemote().
              sendObject(message);
        } catch (IOException | EncodeException e) {
          e.printStackTrace();
        }
      }
    });
  }
}
