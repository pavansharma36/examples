package org.one;

import com.rabbitmq.client.ConnectionFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Base {

  protected static final String QUEUE_NAME = "default-queue";
  protected static final String BROADCAST_EXCHANGE_NAME = "broadcast-exchange";
  private static final String HOST = "localhost";
  private static final String USERNAME = "admin";
  private static final String PASSWORD = "password";

  protected static ConnectionFactory connectionFactory() {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(HOST);
    factory.setUsername(USERNAME);
    factory.setPassword(PASSWORD);
    return factory;
  }

}
