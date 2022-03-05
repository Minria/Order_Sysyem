package com.example.order_system.config;

import com.example.order_system.model.User;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AdminInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session=request.getSession(false);
        if(session==null) {
            return false;
        }
        User user= (User) session.getAttribute("user");
        if(user==null) {
            return false;
        }
        return user.getIsAdmin()==1;
    }
}

// contentType:'application/json',