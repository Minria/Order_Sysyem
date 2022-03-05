package com.example.order_system.dao;

import com.example.order_system.model.Dish;
import com.example.order_system.model.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class OrderMapperTest {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private DishMapper dishMapper;
    @Test
    void addOrderUser() {
        Order order=new Order();
        order.setUserId(1);
        List<Dish> dishes=new ArrayList<>();
//        dishes.add(new Dish(11));
//        dishes.add(new Dish(12));
//        dishes.add(new Dish(13));
        order.setDishes(dishes);
        orderMapper.addOrderUser(order);
        orderMapper.addOrderDish(order);
    }

    @Test
    void addOrderDish() {
        List<Integer> list=new ArrayList<>();
        list.add(11);
        list.add(12);
        System.out.println(list.toString());
    }

    @Test
    void selectAll() {
        List<Order> orderList=orderMapper.selectAll();
        for(Order o:orderList){
            System.out.println(o);
        }
    }

    @Test
    void selectById() {
        List<Order> orderList=orderMapper.selectByUserId(3);
        for(Order o:orderList){
            System.out.println(o);
        }
    }

    @Test
    void changeState() {
        orderMapper.changeState(22,1);
    }


    @Test
    void buildOrder() {
        Order order=orderMapper.buildOrder(20);
        order.setDishes(dishMapper.findDish(20));
        System.out.println(order);
    }
}