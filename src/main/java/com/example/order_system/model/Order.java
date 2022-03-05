package com.example.order_system.model;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class Order {
    private int orderId;
    private int userId;
    private Timestamp time;
    private List<Dish> dishes;
    private int isDone;
}
