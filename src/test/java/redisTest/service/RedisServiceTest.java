package redisTest.service;

import com.hxr.springrediskafka.entity.UserBean;
import com.hxr.springrediskafka.service.redis.RedisServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@ComponentScan({"com.hxr.springrediskafka.config.redis",
        "com.hxr.springrediskafka.config.database",
        "com.hxr.springrediskafka.service.redis"})
@MapperScan("com.hxr.springrediskafka.mapper")
@SpringBootTest
@SpringBootApplication
public class RedisServiceTest {

    @Autowired
    RedisServiceImpl redisService;

    static {
        System.setProperty("mode", "redis");
    }

    @Before
    public void before() {
    }

    @Test
    public void testHashAdd() {
        UserBean userBean = new UserBean();
        userBean.setUserAge(33);
        userBean.setUserName("kevine");
        userBean.setUserAlias("a2");
        userBean.setUserId(2);
        redisService.addHashUser(userBean);
    }

    @Test
    public void testHashGet() {
        for (int i = 1; i <=2 ; i++) {

            redisService.getHashUser(i);
        }
    }

    @Test
    public void testListAdd() {
        List<UserBean> list = new ArrayList<>();
        for (int i = 1; i <=2 ; i++) {
            UserBean userBean = new UserBean();
            userBean.setUserAge(20+i);
            userBean.setUserName("kevine");
            userBean.setUserAlias("a"+i);
            userBean.setUserId(i);
            list.add(userBean);

        }
        redisService.addListUesr(list);
    }
    @Test
    public void testListAddStr() {
        redisService.addListString("chris");
    }
    @Test
    public void getListAddStr() {
        redisService.getListString();
        //redisService.getListUser();
    }
    @Test
    public void insertThenPopTest() {
        redisService.insertThenPop();
        //redisService.getListUser();
    }
}
