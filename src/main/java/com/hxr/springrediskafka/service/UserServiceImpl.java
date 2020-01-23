package com.hxr.springrediskafka.service;

import com.hxr.springrediskafka.entity.UserBean;
import com.hxr.springrediskafka.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "userServiceImpl")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public List<UserBean> findAllUser() {
        return userMapper.findAllUsers();
    }

}
