package com.hxr.springrediskafka.entity;

public class RedisUser {
    private String userName;
    private String pwd;
    private Integer age;
    private Integer id;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public static String getKeyName(String userName) {
        return "userName=" + userName;
    }

    public static String getLoginTimeLock(String username) {
        return "user:loginTime:lock:" + username;
    }

    public static String getLoginCountFailedKey(String username) {
        return "user:loginCount:fail:" + username;

    }
}
