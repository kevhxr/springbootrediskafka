package com.hxr.springrediskafka.service;

import com.hxr.springrediskafka.config.database.DBConfig;
import com.hxr.springrediskafka.entity.UserBean;
import com.hxr.springrediskafka.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.Resource;
import java.util.List;

@Service(value = "userServiceImpl")
public class UserServiceImpl implements UserService {

    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private UserMapper userMapper;

    public UserServiceImpl() {

        logger.debug("debug Im been constructed");
        logger.info("info Im been constructed");
    }

    @Resource(name = DBConfig.PTM_BEAN_NAME)
    PlatformTransactionManager transactionManager;

    @Override
    public List<UserBean> findAllUser() {
        return userMapper.findAllUsers();
    }

    @Override
    public int insertUser(UserBean userBean) {
        int i = 0;
        TransactionStatus txStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            i = userMapper.insertUser(userBean);
            if (userBean.getUserAge() > 55) {
                throw new Exception("invalid age provided");
            }
            transactionManager.commit(txStatus);
        } catch (Exception e) {
            logger.error(e.toString());
            i = 0;
            transactionManager.rollback(txStatus);
        }
        return i;
    }

}
