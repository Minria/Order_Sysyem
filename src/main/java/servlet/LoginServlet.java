package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.UserDao;
import entity.User;
import order_system_util.GSON;
import order_system_util.OrderSystemException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")

public class LoginServlet extends HttpServlet {
    private static final Gson gson =new GsonBuilder().create();
    static class Request{
        public String name;
        public String password;
    }
    static class Response{
        public int ok;
        public String reason;
        public String name;
        public int isAdmin;
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        Response response=new Response();
        try {
            String body= GSON.readBody(req);
            Request request=gson.fromJson(body,Request.class);
            System.out.println("账号->"+request.name);
            System.out.println("密码->"+request.password);
            User user= UserDao.selectByName(request.name);
            if(user==null){
                throw new OrderSystemException("账号或者密码错误");
            }
            if((!user.getPassword().equals(request.password))){
                throw new OrderSystemException("账号或者密码错误");
            }
            response.ok=1;
            response.reason="";
            response.name= user.getName();
            response.isAdmin= user.getIsAdmin();
            req.getSession().setAttribute("user",user);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            response.ok=0;
            response.reason=e.getMessage();
        }finally {
            String ret = gson.toJson(response);
            resp.getWriter().write(ret);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
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
            System.out.println(e.getMessage());
            response.ok=0;
            response.reason=e.getMessage();
        }finally {
            String ret = gson.toJson(response);
            resp.getWriter().write(ret);
        }
    }
}
