package com.example.order_system.dao;

import com.example.order_system.model.Order;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface OrderMapper {
    int addOrderUser(Order order);
    int addOrderDish(Order order);
    int deleteOrderUser(int orderId);
    List<Order> selectAll();
    List<Order> selectByUserId(int id);
    int changeState(int orderId, int isDone);
    Order buildOrder(int orderId);
}
