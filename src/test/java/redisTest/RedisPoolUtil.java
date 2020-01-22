package redisTest;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPoolUtil {
    private static JedisPool pool;

    static {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(5);
        poolConfig.setMaxIdle(1);

        String host = "192.168.1.101";
        int port = 6379;

        pool = new JedisPool(poolConfig, host, port);
    }

    public static Jedis getJedis() {
        Jedis jedis = pool.getResource();
        jedis.auth("1234");
        return jedis;
    }

    public static void close(Jedis jedis) {
        jedis.close();
    }


}
