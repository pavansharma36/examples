package org.one;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

/**
 * Hello world!
 *
 */
@Slf4j
public class Ping {

    public static void main( String[] args )
    {
        try(Jedis jedis = new Jedis()) {
            log.info("{}", jedis.ping("HELLO"));
        }
    }
}
