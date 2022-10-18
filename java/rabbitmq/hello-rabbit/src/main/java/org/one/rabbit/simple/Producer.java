package org.one.rabbit.simple;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.one.Base;

@Slf4j
public class Producer extends Base {

  public static void main(String[] args) throws IOException, TimeoutException {
    ConnectionFactory factory = connectionFactory();
    try (Connection connection = factory.newConnection();
         Channel channel = connection.createChannel()) {
      channel.queueDeclare(QUEUE_NAME, false, false, false, null);
      String message = "Hello World!";
      channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
      log.info(" [x] Sent '{}'", message);
    }
  }

}
