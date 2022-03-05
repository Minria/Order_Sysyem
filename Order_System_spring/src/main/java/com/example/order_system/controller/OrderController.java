package com.example.order_system.controller;


import com.example.order_system.config.OrderSystemException;
import com.example.order_system.dao.DishMapper;
import com.example.order_system.dao.OrderMapper;
import com.example.order_system.model.Dish;
import com.example.order_system.model.Order;
import com.example.order_system.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {
    private final Logger logger= LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private DishMapper dishMapper;

    static class Response{
        public int ok;
        public String reason;
    }

    @RequestMapping("/add")
    @ResponseBody
    public Object add(@RequestBody List<Integer> dishIds, HttpServletRequest req){
        logger.info(String.valueOf(dishIds));
        Response response=new Response();
        try{
            HttpSession session = req.getSession(false);
            if (session == null) {
                throw new OrderSystemException("没有登录");
            }
            User user = (User) req.getSession().getAttribute("user");
            if(user==null){
                throw new OrderSystemException("没有登录");
            }
            Order order=new Order();
            order.setUserId(user.getUserId());
            List<Dish> dishes = new ArrayList<>();
            for (Integer id : dishIds) {
                Dish dish = new Dish();
                dish.setDishId(id);
                dishes.add(dish);
            }
            order.setDishes(dishes);
            orderMapper.addOrderUser(order);
            orderMapper.addOrderDish(order);
            response.ok=1;
            response.reason="";
        }catch (Exception e) {
            e.printStackTrace();
            response.ok = 0;
            response.reason = e.getMessage();
        }
        return response;
    }

    @RequestMapping("/findOrders")
    @ResponseBody
    protected Object findOrders(Integer orderId,HttpServletRequest req) {
        logger.info("orderId:"+orderId);
        Response response=new Response();
        try{
            HttpSession session= req.getSession(false);
            if(session==null){
                throw new OrderSystemException("没有登录");
            }
            User user= (User) req.getSession().getAttribute("user");
            if(user==null){
                throw new OrderSystemException("没有登录");
            }
            if(orderId==null){
                List<Order> orders;
                if(user.getIsAdmin()==0){
                    orders = orderMapper.selectByUserId(user.getUserId());
                }else{
                    orders=orderMapper.selectAll();
                }
                return orders;
            }else{
                Order order = orderMapper.buildOrder(orderId);
                order.setDishes(dishMapper.findDish(orderId));
                if (user.getIsAdmin() == 0 && order.getUserId() != user.getUserId()) {
                    throw new OrderSystemException("只能查看自己的订单");
                }
                return order;
            }
        }catch (Exception e){
            response.ok=0;
            response.reason=e.getMessage();
        }
        return response;
    }

    @RequestMapping("/changeState")
    @ResponseBody
    protected Object changeState(Integer orderId,Integer isDone,HttpServletRequest req) {
        Response response=new Response();
        try{
            HttpSession session = req.getSession(false);
            if(session==null){
                throw new OrderSystemException("没有登录");
            }
            User user = (User) session.getAttribute("user");
            if (user == null){
                throw new OrderSystemException("没有登录");
            }
            if(user.getIsAdmin()==0){
                throw new OrderSystemException("您不是管理员");
            }
            if (orderId == null || isDone == null) {
                throw new OrderSystemException("参数有误");
            }else{
                orderMapper.changeState(orderId, isDone);
                response.ok = 1;
                response.reason="";
            }
        }catch (Exception e){
            response.ok=0;
            response.reason=e.getMessage();
        }
        return response;
    }
}
