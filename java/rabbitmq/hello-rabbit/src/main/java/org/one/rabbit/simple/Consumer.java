package org.one.rabbit.simple;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.one.Base;

@Slf4j
public class Consumer extends Base {

  public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
    ConnectionFactory factory = connectionFactory();
    try (Connection connection = factory.newConnection();
         Channel channel = connection.createChannel()) {
      CountDownLatch cl = new CountDownLatch(1);
      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), "UTF-8");
        log.info("Received message '{}'", message);
        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        cl.countDown();
      };
      channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> { });
      cl.await();
    }
  }

}
