package com.hxr.springrediskafka.entity;

public class UserBean {

    private String userName;
    private int userId;
    private int userAge;
    private String userAlias;

    public String getUserAlias() {
        return userAlias;
    }

    public void setUserAlias(String alias) {
        this.userAlias = alias;
    }

    public int getUserAge() {
        return userAge;
    }

    public void setUserAge(int userAge) {
        this.userAge = userAge;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "userName='" + userName + '\'' +
                ", userId=" + userId +
                ", userAge=" + userAge +
                ", userAlias='" + userAlias + '\'' +
                '}';
    }
}