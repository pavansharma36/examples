package org.one.jedis.pubsub;

import lombok.extern.slf4j.Slf4j;
import org.one.Base;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

@Slf4j
public class Subscriber extends Base {

  public static void main(String[] args) {
    try (Jedis jedis = new Jedis()) {
      jedis.subscribe(new JedisPubSub() {

        @Override
        public void onSubscribe(String channel, int subscribedChannels) {
          super.onSubscribe(channel, subscribedChannels);
          log.info("Subscribed to topic '{}'", channel);
        }

        @Override
        public void onMessage(String channel, String message) {
          log.info("Received '{}' on channel '{}'", message, channel);

          // subscribe is blocking operation, unsubscribe to exit program.
          unsubscribe();
        }
      }, BROADCAST_TOPIC_NAME);
    }
  }
}
