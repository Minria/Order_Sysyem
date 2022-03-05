package com.example.order_system.model;

import lombok.Data;

@Data
public class Dish {
    private int dishId;
    private String name;
    private int price;
    private int state;
}
