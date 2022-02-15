package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.OrderDao;
import entity.Dish;
import entity.Order;
import entity.User;
import order_system_util.GSON;

import javax.servlet.ServletException;
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        Response response = new Response();
        HttpSession session = req.getSession(false);
        if (session == null) {
            response.ok = 0;
            response.reason = "没有登录";
        } else {
            User user = (User) req.getSession().getAttribute("user");
            if (user == null) {
                response.ok = 0;
                response.reason = "没有登录";
            } else {
                String body = GSON.readBody(req);
                Integer[] dishId = gson.fromJson(body, Integer[].class);
                Order order = new Order();
                order.setUserId(user.getId());
                List<Dish> dishes = new ArrayList<>();
                for (Integer id : dishId) {
                    Dish dish = new Dish();
                    dish.setId(id);
                    dishes.add(dish);
                }
                order.setDishes(dishes);
                OrderDao.add(order);
                response.ok = 1;
                response.reason = "";
            }
        }
        String ret = gson.toJson(response);
        resp.getWriter().write(ret);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        Response response=new Response();
        List<Order> orders=null;
        HttpSession session= req.getSession(false);
        if(session==null){
            response.ok=0;
            response.reason="没有登录";
            System.out.println("没有登录");
        }else{
            User user= (User) req.getSession().getAttribute("user");
            if(user==null){
                System.out.println("没有登录");
                response.ok=0;
                response.reason="没有登录";
            }else{
                String orderIdStr = req.getParameter("orderId");
                if(orderIdStr==null){
                    if(user.getIsAdmin()==0){
                        orders = OrderDao.selectByUserId(user.getId());
                    }else{
                        orders=OrderDao.selectAll();
                    }
                    String ret = gson.toJson(orders);
                    resp.getWriter().write(ret);
                }else{
                    int orderId = Integer.parseInt(orderIdStr);
                    Order order = OrderDao.selectById(orderId);
                    if (user.getIsAdmin() == 0 && order.getUserId() != user.getId()) {
                        response.ok=0;
                        response.reason="只能查看自己的订单";
                    }
                    String ret = gson.toJson(response);
                    resp.getWriter().write(ret);
                }
            }
        }
    }


    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        Response response=new Response();
        HttpSession session = req.getSession(false);
        if(session==null){
            response.ok=0;
            response.reason="没有登录";
        }else{
            User user = (User) session.getAttribute("user");
            if (user == null) {
                response.ok=0;
                response.reason="没有登录";
            }else{
                if(user.getIsAdmin()==0) {
                    response.ok = 0;
                    response.reason = "您不是管理员";
                }else {
                    String orderIdStr = req.getParameter("orderId");
                    String isDoneStr = req.getParameter("isDone");
                    if (orderIdStr == null || isDoneStr == null) {
                        response.ok=0;
                        response.reason="参数有误";
                    }else{
                        int orderId = Integer.parseInt(orderIdStr);
                        int isDone = Integer.parseInt(isDoneStr);
                        OrderDao.changeState(orderId, isDone);
                        response.ok = 1;
                    }
                }
            }
        }
        String ret = gson.toJson(response);
        resp.getWriter().write(ret);
    }
}
