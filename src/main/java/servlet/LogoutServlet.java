package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private static final Gson gson=new GsonBuilder().create();
    static class Response{
        public int ok;
        public String reason;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        Response response=new Response();
        HttpSession session= req.getSession(false);
        if(session==null){
            System.out.println("尚未登录");
            response.ok=0;
            response.reason="尚未登录";
        }else{
            req.removeAttribute("user");
            response.ok=1;
            response.reason="";
        }
        String ret = gson.toJson(response);
        resp.getWriter().write(ret);
    }
}
