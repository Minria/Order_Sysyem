package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.DishDao;
import entity.Dish;
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

@WebServlet("/dish")

public class DishServlet extends HttpServlet {
    private static final Gson gson = new GsonBuilder().create();

    static class Request {
        public String name;
        public int price;
    }

    static class Response {
        public int ok;
        public String reason;
    }

    //新增菜品
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        Response response=new Response();
        HttpSession session= req.getSession(false);
        if(session==null){
            response.ok=0;
            response.reason="请登录";
        }else{
            User user= (User) req.getSession().getAttribute("user");
            if(user==null){
                response.ok=0;
                response.reason="请登录";
            }else{
                if(user.getIsAdmin()==0){
                    response.ok=0;
                    response.reason="您不是管理员";
                }else{
                    response.ok=1;
                    String body= GSON.readBody(req);
                    Request request=gson.fromJson(body,Request.class);
                    Dish dish=new Dish();
                    dish.setName(request.name);
                    dish.setPrice(request.price);
                    DishDao.add(dish);
                    response.reason="";
                    System.out.println("新增菜品成功");
                }
            }
        }
        String ret = gson.toJson(response);
        resp.getWriter().write(ret);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        Response response=new Response();
        HttpSession session= req.getSession(false);
        if(session==null){
            response.ok=0;
            response.reason="没有登录";
        }else{
            User user= (User) req.getSession().getAttribute("user");
            if(user==null){
                response.ok=0;
                response.reason="没有登录";
                System.out.println("没有登录");
            }else{
                if(user.getIsAdmin()==0){
                    response.ok=0;
                    response.reason="不是管理员";
                    System.out.println("不是管理员");
                }else{
                    String dishStrId=req.getParameter("dishId");
                    System.out.println("字符串格式->"+dishStrId);
                    if(dishStrId==null){
                        response.ok=0;
                        response.reason="参数不正确";
                    }else{
                        int id=Integer.parseInt(dishStrId);
                        System.out.println("数字格式->"+dishStrId);
                        DishDao.delete(id);
                        response.ok=1;
                        response.reason="";
                    }
                }
            }
        }
        String jsonString = gson.toJson(response);
        resp.getWriter().write(jsonString);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        List<Dish> dishes=new ArrayList<>();
        HttpSession session= req.getSession(false);
        if(session==null) {
            String jsonString = gson.toJson(dishes);
            resp.getWriter().write(jsonString);
        }else{
            User user= (User) req.getSession().getAttribute("user");
            if(user==null) {
                String jsonString = gson.toJson(dishes);
                resp.getWriter().write(jsonString);
            }else{
                dishes=DishDao.selectAll();
                String jsonString = gson.toJson(dishes);
                resp.getWriter().write(jsonString);
            }
        }
    }
}
