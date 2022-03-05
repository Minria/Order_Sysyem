package com.example.order_system.model;


import lombok.Data;

@Data
public class User {
    private int userId;
    private String name;
    private String password;
    private int isAdmin;
}
