package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.OrderDao;
import entity.Dish;
import entity.Order;
import entity.User;
import order_system_util.GSON;
import order_system_util.OrderSystemException;


import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@WebServlet("/order")
public class OrderServlet extends HttpServlet {
    private static final Gson gson=new GsonBuilder().create();
    static class Response{
        public int ok;
        public String reason;
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        Response response = new Response();
        try{
            HttpSession session = req.getSession(false);
            if (session == null) {
                throw new OrderSystemException("没有登录");
            }
            User user = (User) req.getSession().getAttribute("user");
            if(user==null){
                throw new OrderSystemException("没有登录");
            }
            String body = GSON.readBody(req);
            Integer[] dishId = gson.fromJson(body, Integer[].class);
            Order order = new Order();
            order.setUserId(user.getUserId());
            List<Dish> dishes = new ArrayList<>();
            for (Integer id : dishId) {
                Dish dish = new Dish();
                dish.setDishId(id);
                dishes.add(dish);
            }
            order.setDishes(dishes);
            OrderDao.add(order);
            response.ok = 1;
            response.reason = "";
        }catch (Exception e){
            e.printStackTrace();
            response.ok=0;
            response.reason=e.getMessage();
        }finally {
            String ret = gson.toJson(response);
            resp.getWriter().write(ret);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        Response response=new Response();
        List<Order> orders=null;
        try{
            HttpSession session= req.getSession(false);
            if(session==null){
                throw new OrderSystemException("没有登录");
            }
            User user= (User) req.getSession().getAttribute("user");
            if(user==null){
                throw new OrderSystemException("没有登录");
            }
            String orderIdStr = req.getParameter("orderId");
            if(orderIdStr==null){
                if(user.getIsAdmin()==0){
                    orders = OrderDao.selectByUserId(user.getUserId());
                }else{
                    orders=OrderDao.selectAll();
                }
                String ret = gson.toJson(orders);
                resp.getWriter().write(ret);
            }else{
                int orderId = Integer.parseInt(orderIdStr);
                System.out.println("订单id->"+orderId);
                Order order = OrderDao.selectById(orderId);
                if (user.getIsAdmin() == 0 && order.getUserId() != user.getUserId()) {
                    throw new OrderSystemException("只能查看自己的订单");
                }
                String ret = gson.toJson(order);
                resp.getWriter().write(ret);
            }
        }catch (Exception e){
            String jsonString = gson.toJson(orders);
            resp.getWriter().write(jsonString);
        }
    }


    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
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
            String orderIdStr = req.getParameter("orderId");
            String isDoneStr = req.getParameter("isDone");
            if (orderIdStr == null || isDoneStr == null) {
                throw new OrderSystemException("参数有误");
            }else{
                int orderId = Integer.parseInt(orderIdStr);
                int isDone = Integer.parseInt(isDoneStr);
                OrderDao.changeState(orderId, isDone);
                response.ok = 1;
                response.reason="";
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            response.ok=0;
            response.reason=e.getMessage();
        }finally {
            String ret = gson.toJson(response);
            resp.getWriter().write(ret);
        }
    }
}
