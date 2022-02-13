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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        Response response=new Response();
        HttpSession session= req.getSession(false);
        if(session==null){
            response.ok=0;
            response.reason="请登录";
        }else{
            User user= (User) req.getSession().getAttribute("user");
            if(user.getIdAdmin()==0){
                response.ok=0;
                response.reason="您不是管理员";
            }else{
                response.ok=1;
                response.reason="";
                String body= GSON.readBody(req);
                Request request=gson.fromJson(body,Request.class);
                Dish dish=new Dish();
                dish.setName(request.name);
                dish.setPrice(request.price);
                DishDao.add(dish);

            }
        }
    }
}
