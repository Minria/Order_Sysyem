package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.UserDao;
import entity.User;
import order_system_util.GSON;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet("/register")

public class RegisterServlet extends HttpServlet {
    private final Gson gson = new GsonBuilder().create();
    static class Request{
        String name;
        String password;
    }
    static class Response{
        public int ok;
        public String reason;
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        Response response=new Response();
        String body= GSON.readBody(req);
        Request request = gson.fromJson(body, Request.class);
        User existUser= UserDao.selectByName(request.name);
        if(existUser!=null) {
            response.ok = 0;
            response.reason = "当前用户名已经存在";
        }else{
            User newUser=new User();
            newUser.setName(request.name);
            newUser.setPassword(request.password);
            newUser.setIdAdmin(0);
            UserDao.add(newUser);
            response.ok=1;
        }
        String ret = gson.toJson(response);
        resp.getWriter().write(ret);
    }
}
