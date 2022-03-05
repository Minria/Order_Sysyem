package com.example.order_system.dao;

import com.example.order_system.model.Dish;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DishMapperTest {

    @Autowired
    private DishMapper dishMapper;
    @Test
    void add() {
        Dish dish=new Dish();
        dish.setName("烤面筋");
        dish.setPrice(300);
        dish.setState(0);
        System.out.println(dishMapper.add(dish));
    }

    @Test
    void update() {
        int res = dishMapper.update(18,1);
        System.out.println(res);
    }

    @Test
    void selectAll() {
        List<Dish> list=dishMapper.selectAll();
        for (Dish dish : list) {
            System.out.println(dish);
        }
    }

    @Test
    void selectById() {
        System.out.println(dishMapper.selectById(19));
    }

    @Test
    void selectByName() {
        System.out.println(dishMapper.selectByName("烤面筋"));
    }

    @Test
    void findDish() {
        List<Dish> dishes=dishMapper.findDish(20);
        for(Dish d:dishes){
            System.out.println(d);
        }
    }
}