package com.example.order_system.dao;

import com.example.order_system.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;
    @Test
    void add() {
        User user=new User();
        user.setName("test");
        user.setPassword("test");
        user.setIsAdmin(0);
        int res = userMapper.add(user);
        System.out.println(res);
        System.out.println(user);
    }

    @Test
    void delete() {
        int id = 4;
        int res = userMapper.delete(id);
        System.out.println(res);
    }

    @Test
    void selectById() {
        int id = 4;
        User user=userMapper.selectById(id);
        System.out.println(user);
    }

    @Test
    void selectByName() {
        System.out.println(userMapper.selectByName("wfm"));
        System.out.println(userMapper.selectByName("user111"));
        System.out.println(userMapper.selectByName("test"));
    }
}