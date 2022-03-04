package order_system_util;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class GSON {
    public static String readBody(HttpServletRequest request)  {
        int length=request.getContentLength();
        byte[] buffer=new byte[length];
        try(InputStream inputStream= request.getInputStream()){
            inputStream.read(buffer,0,length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(buffer, StandardCharsets.UTF_8);
    }
}
