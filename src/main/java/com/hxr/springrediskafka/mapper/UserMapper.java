package com.hxr.springrediskafka.mapper;


import com.hxr.springrediskafka.entity.UserBean;

import java.util.List;

public interface UserMapper {

    List<UserBean> findAllUsers();
}
