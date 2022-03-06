package com.example.order_system.controller;


import com.example.order_system.config.OrderSystemException;
import com.example.order_system.dao.UserMapper;
import com.example.order_system.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpSession;


@Controller
@RequestMapping("/user")
public class UserController {

    private final Logger logger= LoggerFactory.getLogger(UserController.class);
    static class Response{
        public int ok;
        public String reason;
        public String name;
        public int isAdmin;
    }
    @Autowired
    private UserMapper userMapper;

    @PostMapping ("/login")
    @ResponseBody
    public Object login(@RequestBody User loginUser,
                        HttpServletRequest request){
        logger.info("登录用户名："+loginUser.getName()+" 登录密码："+loginUser.getPassword());
        Response response=new Response();
        try{
            User user=userMapper.selectByName(loginUser.getName());
            if(user==null||!user.getPassword().equals(loginUser.getPassword())){
                throw new OrderSystemException("账号或者密码错误");
            }
            request.getSession().setAttribute("user",user);
            response.ok=1;
            response.reason="";
            response.name= user.getName();
            response.isAdmin=user.getIsAdmin();
        }catch (Exception e){
            response.ok=0;
            response.reason=e.getMessage();
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("/isLogin")
    public Object isLogin(HttpServletRequest req) {
        Response response=new Response();
        try{
            HttpSession httpSession= req.getSession(false);
            if(httpSession==null){
                throw new OrderSystemException("未登录");
            }
            User user= (User) req.getSession().getAttribute("user");
            if(user==null){
                throw new OrderSystemException("未登录");
            }
            response.ok=1;
            response.reason="";
            response.name= user.getName();
            response.isAdmin= user.getIsAdmin();
        }catch (Exception e){
            response.ok=0;
            response.reason=e.getMessage();
        }
        return response;
    }
    @RequestMapping("/logout")
    public void logout(HttpServletRequest request){
        logger.info("尝试注销");
        HttpSession session= request.getSession(false);
        session.removeAttribute("user");
    }

    @RequestMapping("/register")
    @ResponseBody
    public Object register(@RequestBody User user){
        Response response=new Response();
        try{
            if(user.getName()==null||user.getName().length()==0){
                throw new OrderSystemException("输入用户名");
            }
            User findUser=userMapper.selectByName(user.getName());
            if(findUser!=null){
                throw new OrderSystemException("当前用户名已经存在");
            }
            if(user.getPassword()==null||user.getPassword().length()==0){
                throw new OrderSystemException("密码为空");
            }
            int res = userMapper.add(user);
            if(res==0){
                throw new OrderSystemException("注册失败，原因未知");
            }
            response.ok=1;
            response.reason="";
        }catch (Exception e){
            response.ok=0;
            response.reason=e.getMessage();
        }
        return response;

    }
}
