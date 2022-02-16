package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.DishDao;
import entity.Dish;
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
        try{
            HttpSession session= req.getSession(false);
            if(session==null){
                throw new OrderSystemException("请登录");
            }
            User user= (User) req.getSession().getAttribute("user");
            if(user==null){
                throw new OrderSystemException("请登录");
            }
            if(user.getIsAdmin()==0){
                throw new OrderSystemException("您不是管理员");
            }
            String body= GSON.readBody(req);
            Request request=gson.fromJson(body,Request.class);
            Dish dish=new Dish();
            dish.setName(request.name);
            dish.setPrice(request.price);
            DishDao.add(dish);
            response.ok=1;
            response.reason="";
            System.out.println("新增菜品成功");
        }catch (Exception e){
            e.printStackTrace();
            response.ok=0;
            response.reason=e.getMessage();
        } finally {
            String ret = gson.toJson(response);
            resp.getWriter().write(ret);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
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
            if(user.getIsAdmin()==0){
                throw new OrderSystemException("您不是管理员");
            }
            String dishStrId=req.getParameter("dishId");
            System.out.println("字符串格式->"+dishStrId);
            if(dishStrId==null){
                throw new OrderSystemException("参数不正确");
            }
            int id=Integer.parseInt(dishStrId);
            System.out.println("数字格式->"+dishStrId);
            DishDao.delete(id);
            response.ok=1;
            response.reason="";
        }catch (Exception e){
            System.out.println("DishServlet_doDelete:");
            System.out.println(e.getMessage());
            e.printStackTrace();
            response.ok=0;
            response.reason=e.getMessage();
        } finally {
            String jsonString = gson.toJson(response);
            resp.getWriter().write(jsonString);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        List<Dish> dishes=new ArrayList<>();
        try{
            HttpSession session= req.getSession(false);
            if(session==null){
                throw new OrderSystemException("没有登录");
            }
            User user= (User) req.getSession().getAttribute("user");
            if(user==null){
                throw new OrderSystemException("没有登录");
            }
            dishes=DishDao.selectAll();
            String ret = gson.toJson(dishes);
            resp.getWriter().write(ret);
        }catch (Exception e){
            e.printStackTrace();
            String ret = gson.toJson(dishes);
            resp.getWriter().write(ret);
        }
    }
}
