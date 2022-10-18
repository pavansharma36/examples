package org.one.rabbit.broadcast;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.one.Base;

/**
 * create exchange with fanout type and send message to that exchange with empty routing key.
 */
@Slf4j
public class Publisher extends Base {

  public static void main(String[] args) throws IOException, TimeoutException {
    ConnectionFactory factory = connectionFactory();
    try (Connection connection = factory.newConnection();
         Channel channel = connection.createChannel()) {
      String message = "Hello World!";
      channel.basicPublish(BROADCAST_EXCHANGE_NAME, "", null, message.getBytes());
      log.info(" [x] Sent '{}'", message);
    }
  }

}
