package com.example.order_system.controller;


import com.example.order_system.config.OrderSystemException;
import com.example.order_system.dao.DishMapper;
import com.example.order_system.model.Dish;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@ResponseBody
@RequestMapping("/dish")
public class DishController {
    public DishController(DishMapper dishMapper) {
        this.dishMapper = dishMapper;
    }

    static class Response {
        public int ok;
        public String reason;
    }

    private final Logger logger= LoggerFactory.getLogger(DishController.class);
    private final DishMapper dishMapper;
    @RequestMapping("/add")
    public Object add(@RequestBody Dish dish){
        logger.info(dish.toString());
        Response response=new Response();
        try {
            Dish findDish=dishMapper.selectByName(dish.getName());
            if(findDish!=null){
                if(findDish.getState()==0){
                    throw new OrderSystemException("当前菜名已经存在;或删除后添加");
                }else{
                    if(findDish.getPrice()==dish.getPrice()){
                        dishMapper.update(findDish.getDishId(), 0);
                        response.ok=1;
                        response.reason="";
                        System.out.println("新增菜品成功");
                        return response;
                    }
                }
            }
            Dish dish2=new Dish();
            dish2.setName(dish.getName());
            dish2.setPrice(dish.getPrice());
            dishMapper.add(dish);
            response.ok=1;
            response.reason="";
            System.out.println("新增菜品成功");
        }catch (Exception e){
            response.ok=0;
            response.reason=e.getMessage();
        }
        return response;
    }

    @RequestMapping("delete")
    public Object delete(Integer dishId){
        Response response=new Response();
        try{
            dishMapper.update(dishId,1);
            response.ok=1;
            response.reason="";
        }catch (Exception e){
            response.reason=e.getMessage();
            response.ok=0;
        }
        return response;
    }


    @RequestMapping("/getDish")
    public Object getDish(){
        Response response=new Response();
        try{
            List<Dish> dishes=dishMapper.selectAll();
            response.ok=0;
            return dishes;
        }catch (Exception e){
            response.reason=e.getMessage();
            response.ok=0;
        }
        return response;
    }
}
