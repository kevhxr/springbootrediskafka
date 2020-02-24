package com.hxr.springrediskafka.service.redis;

import com.hxr.springrediskafka.config.annotation.ConditionalOnSystemProperty;
import com.hxr.springrediskafka.entity.RedisUser;
import com.hxr.springrediskafka.entity.UserBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@ConditionalOnSystemProperty(name = "mode", value = "redis")
public class RedisServiceImpl implements RedisService {

    @Resource(name = "redisTemplate")
    RedisTemplate template;

    HashOperations<String, Integer, UserBean> hashOps = template.opsForHash();

    @Resource(name = "redisTemplate")
    ListOperations<String, UserBean> listOps;

    @Resource(name = "redisTemplate")
    ListOperations<String, String> stringListOps;

    @Override
    public void addNewKey() {
        String key = "aa";
        if (template.hasKey(key)) {
            System.out.println("get key from redis");
            System.out.println(template.opsForValue().get(key));
        } else {
            System.out.println("store key first");
            template.opsForValue().set(key, "sdfgg");
        }
    }

    public void setTemplate(RedisTemplate template) {
        this.template = template;
    }

    @Override
    public RedisUser login(String userName, String pwd) {
        RedisUser user = null;
        String key = RedisUser.getKeyName(userName);
        if (template.hasKey(key)) {
            String password = String.valueOf(template.opsForValue().get(key));
            if (pwd.equals(password)) {
                user = new RedisUser();
                user.setPwd(pwd);
                user.setUserName(userName);
            }
        }
        return user;
    }

    @Override
    public String loginValidateAfterFailed(String name) {
        String key = RedisUser.getLoginCountFailedKey(name);
        //failed login limit
        int num = 5;
        //if user has login failed attempt
        if (template.hasKey(key)) {
            Integer loginFailedCount = (Integer) template.opsForValue().get(key);
            if (loginFailedCount < (num - 1)) {
                template.opsForValue().increment(key, 1);
                return name + " login failed, allow retry in " +
                        template.getExpire(key, TimeUnit.SECONDS)
                        + "second, allowed retry times left:" + (num - loginFailedCount - 1);
            } else {
                //over login failed limit
                //so lock user for login this account for 1 MINUTE
                String lockKey = RedisUser.getLoginTimeLock(name);
                template.opsForValue().set(lockKey, 1);
                template.expire(lockKey, 1, TimeUnit.MINUTES);
                return name + " login failed count exceed limit been locked 4 1 minutes";
            }
        } else {
            //cannot set expire time while set value
            //record login failed 4 the first time
            template.opsForValue().set(key, 1);
            template.expire(key, 20, TimeUnit.SECONDS);
            return name + " login failed, now allow login retry times:" + (num - 1);

        }
    }

    @Override
    public Map<String, Object> loginUserLock(String name) {
        System.out.println(serviceName);
        Map<String, Object> map = new HashMap();
        String key = RedisUser.getLoginTimeLock(name);
        if (template.hasKey(key)) {
            Long expireTime = template.getExpire(key, TimeUnit.SECONDS);
            map.put("flag", true);
            map.put("lockTime", expireTime);
        } else {
            map.put("flag", false);
        }
        return map;
    }

    @Override
    public void addHashUser(UserBean userBean) {
        hashOps.put("user", userBean.getUserId(), userBean);
    }

    @Override
    public void getHashUser(Integer id) {
        UserBean user = hashOps.get("user", id);
        if (user != null) {
            System.out.println(user);
        }
    }

    public void addListUesr(List<UserBean> users){
        users.stream().forEach(user-> listOps.leftPush("userList",user));
    }

    public void addListString(String str){
        stringListOps.leftPush("userList",str);
    }

    public void getListString(){
        String userList = stringListOps.leftPop("userList");
        System.out.println(userList);
    }

    public void getListUser(){
        UserBean userList = listOps.leftPop("userList");
        System.out.println(userList);
    }

    public void insertThenPop(){
        template.setEnableTransactionSupport(true);
        template.multi();
        List exec = new ArrayList();
        int a = 0;
        ListOperations<String, Integer> vo = template.opsForList();
        try{
            vo.leftPush("mylist",12);
            vo.leftPush("mylist",13);
            vo.leftPop("mylist");
            vo.leftPush("mylist",null);
            if(a == 0) {
                throw new Exception("sd");
            }
            exec = template.exec();
        }catch (Exception e){
            System.out.println(e.toString());
            template.discard();
        }
        exec.forEach(result->{
            System.out.println(result.toString());
        });
    }

    public void doSet(){
        SetOperations set = template.opsForSet();
        set.add("A:followers","B");
        set.add("A:followers","C");
        set.add("B:followers","C");
        set.add("B:followers","A");

        Set intersect = set.intersect("A:followers", "B:followers");
        Set difference = set.difference("A:followers", "B:followers");
        Set union = set.union("A:followers", "B:followers");
        String randomMember = (String) set.randomMember("A:followers");
        intersect.stream().forEach(a-> System.out.println(a));
        System.out.println("============");
        difference.stream().forEach(a-> System.out.println(a));
        System.out.println("============");
        union.stream().forEach(a-> System.out.println(a));
        System.out.println("============");
        System.out.println(randomMember);
    }
}
