package com.hxr.springrediskafka.service;

import com.hxr.springrediskafka.entity.UserBean;

import java.util.List;

public interface UserService {

    List<UserBean> findAllUser();

    int insertUser(UserBean userBean);
}
