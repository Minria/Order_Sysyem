package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.UserDao;
import entity.User;
import order_system_util.GSON;

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
        String name;
        String password;
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
        String body= GSON.readBody(req);
        Request request=gson.fromJson(body,Request.class);
        Response response=new Response();
        User user= UserDao.selectByName(request.name);
        if(user==null){
            System.out.println("账号或者密码错误");
            response.ok=0;
            response.reason="账号或者密码错误";
        }else{
            if(user.getPassword().equals(request.password)){
                response.ok=1;
                response.reason="登录成功";
                response.name= user.getName();
                response.isAdmin= user.getIdAdmin();
                req.getSession().setAttribute("user",user);
            }else{
                System.out.println("账号或者密码错误");
                response.ok=0;
                response.reason="账号或者密码错误";
            }
        }
        String ret = gson.toJson(response);
        resp.getWriter().write(ret);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        HttpSession httpSession= req.getSession(false);
        Response response=new Response();
        if(httpSession==null){
            System.out.println("未登录");
            response.ok = 0;
            response.reason = "未登录";
        }else{
            User user= (User) req.getSession().getAttribute("user");
            if(user==null){
                System.out.println("未登录");
                response.ok = 0;
                response.reason = "未登录";
            }else{
                response.ok=1;
                response.reason="";
                response.name= user.getName();
                response.isAdmin= user.getIdAdmin();
            }
        }
        String ret = gson.toJson(response);
        resp.getWriter().write(ret);
    }
}