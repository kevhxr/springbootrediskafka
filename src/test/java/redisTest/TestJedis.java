package redisTest;

import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

public class TestJedis {

    public static void main(String[] args) {
        Jedis jedis = RedisPoolUtil.getJedis();
    }

    @Test
    public void t1() {
        Jedis jedis = RedisPoolUtil.getJedis();
        jedis.set("strName", "strVal");
        String strName = jedis.get("strName");
        System.out.println(strName);
        jedis.close();
    }


    @Test
    public void t2() {
        Jedis jedis = RedisPoolUtil.getJedis();
        String key = "appName";
        if (jedis.exists(key)) {
            String s = jedis.get(key);
            System.out.println("Get from redis:" + s);
        } else {
            String result = "mywechat";
            jedis.set(key, result);
            System.out.println("Get from DB:" + result);
        }
        jedis.close();
    }



    @Test
    public void t3() {
        Jedis jedis = RedisPoolUtil.getJedis();

        String key = "users";
        if(jedis.exists(key)){
            Map<String, String> map = jedis.hgetAll(key);
            System.out.println("Get from Redis:");
            System.out.println(map.get("id")+",\t"+map.get("name")+",\t"+map.get("age")+",\t"+map.get("remark"));
        }else{
            jedis.hset(key,"id","1");
            jedis.hset(key,"name","kevin");
            jedis.hset(key,"age","22");
            jedis.hset(key,"remark","great man");
            System.out.println("Set to DB");
        }
        jedis.close();
    }




    @Test
    public void t4() {
        Jedis jedis = RedisPoolUtil.getJedis();

        String key = "user:2";

        if(jedis.exists(key)){
            Map<String, String> map = jedis.hgetAll(key);
            User newUser = new User();

            newUser.setId(Integer.parseInt(map.get("id")));
            newUser.setAge(Integer.parseInt(map.get("age")));
            newUser.setName(map.get("name"));
            newUser.setRemark(map.get("remark"));
            System.out.println("Get from redis:");
            System.out.println(newUser.toString());
        }else{
            User newUser = new User();
            newUser.setId(1);
            newUser.setAge(22);
            newUser.setName("chris");
            newUser.setRemark("chicken lover");
            HashMap<String, String> map = new HashMap<>();
            map.put("id",newUser.getId()+"");
            map.put("age",newUser.getAge()+"");
            map.put("name",newUser.getName()+"");
            map.put("remark",newUser.getRemark()+"");
            jedis.hmset(key, map);

        }

        jedis.close();
    }

}
