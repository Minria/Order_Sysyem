package com.example.order_system.dao;

import com.example.order_system.model.Dish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishMapper {
    int add(Dish dish);
    int update(int id,int state);
    List<Dish> selectAll();
    Dish selectById(int id);
    Dish selectByName(String name);
    List<Dish> findDish(int orderId);
}
