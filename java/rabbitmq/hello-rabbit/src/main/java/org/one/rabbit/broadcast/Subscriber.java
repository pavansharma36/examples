package org.one.rabbit.broadcast;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.one.Base;


/**
 * A pattern in which all running nodes can receive messages.
 * Can be useful in building inmemory cache clearing requirement.
 * Or general broadcast message.
 *
 * declare queue without any name with auto delete.
 * and bind this queue to broadcast exchange to start receiving messages from.
 */
@Slf4j
public class Subscriber extends Base {

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

      String queueName = channel.queueDeclare().getQueue();
      log.info("Created queue '{}' for current process", queueName);
      channel.queueBind(queueName, BROADCAST_EXCHANGE_NAME, "");

      channel.basicConsume(queueName, false, deliverCallback, consumerTag -> { });
      cl.await();
    }
  }
}
