package redisTest;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import redisTest.service.UserService;


public class RedisTemplateTest {

    @Test
    public void t1(){
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring_redis.xml");
        UserService userService = ctx.getBean(UserService.class);
        String key = "testKey1";
        String result = userService.getUserFromCache(key);
        System.out.println(result);
    }
}
