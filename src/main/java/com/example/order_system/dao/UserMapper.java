package com.example.order_system.dao;

import com.example.order_system.model.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    int add(User user);
    int delete(int id);
    User selectById(int id);
    User selectByName(String name);
}
