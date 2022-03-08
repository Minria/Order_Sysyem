package com.example.order_system.component;

import com.example.order_system.config.OrderSystemException;
import org.springframework.stereotype.Component;



@Component
public class Util {
    public void verifyPassword(String name) throws OrderSystemException {
        if(name==null){
            throw new OrderSystemException("请输入密码");
        }
        int len=name.length();
        if(len<6){
            throw new OrderSystemException("密码长度至少为6，且包含字母和数字");
        }
        boolean isHaveNumber=false;
        boolean isHaveChar=false;
        for(int i=0;i<len;i++){
            char tmp=name.charAt(i);
            if(!isHaveNumber&&tmp<='9'&&tmp>='0'){
                isHaveNumber=true;
            }
            if(!isHaveChar&&((tmp>='a'&&tmp<='z')||(tmp>='A'&&tmp<='Z'))){
                isHaveChar=true;
            }
        }
        if(!isHaveChar){
            throw new OrderSystemException("密码长度至少为6，且包含字母和数字,当前密码不包含字母");
        }
        if(!isHaveNumber){
            throw new OrderSystemException("密码长度至少为6，且包含字母和数字,当前密码不包含数字");
        }
    }
}
