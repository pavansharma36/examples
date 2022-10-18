package org.one.jedis.pubsub;

import lombok.extern.slf4j.Slf4j;
import org.one.Base;
import org.one.Ping;
import redis.clients.jedis.Jedis;

@Slf4j
public class Publisher extends Base {

  public static void main(String[] args) {
    try(Jedis jedis = new Jedis()) {
      jedis.publish(BROADCAST_TOPIC_NAME, "Hello world");
    }
  }

}
