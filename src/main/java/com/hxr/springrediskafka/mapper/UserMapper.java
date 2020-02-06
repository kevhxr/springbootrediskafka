package com.hxr.springrediskafka.mapper;


import com.hxr.springrediskafka.entity.UserBean;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper()
public interface UserMapper {

    List<UserBean> findAllUsers();

    int insertUser(UserBean userBean);
}
